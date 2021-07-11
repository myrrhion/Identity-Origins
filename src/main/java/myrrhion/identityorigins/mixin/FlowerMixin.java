package myrrhion.identityorigins.mixin;

import io.github.apace100.origins.component.OriginComponent;
import myrrhion.identityorigins.powers.EdiblePower;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public abstract class FlowerMixin {

    private final double biteDamage = 29;
    @Unique
    private Entity owner;
    @Shadow
    public abstract FoodComponent getFoodComponent();

    @Inject(method = "getUseAction", at = @At("RETURN"), cancellable = true)
    public void getOmnivoreUseAction(ItemStack stack, CallbackInfoReturnable<UseAction> cir) {
       if(OriginComponent.getPowers(owner, EdiblePower.class).stream().anyMatch(p -> p.doesApply(stack)))
             cir.setReturnValue(UseAction.EAT);
    }

    @Inject(method = "getMaxUseTime", at = @At("RETURN"), cancellable = true)
    public void getOmnivoreUseTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if(OriginComponent.getPowers(owner, EdiblePower.class).stream().anyMatch(p -> p.doesApply(stack)))
            cir.setReturnValue(48);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void omnivoreUse(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = player.getStackInHand(hand);

        if(OriginComponent.getPowers(player, EdiblePower.class).stream().anyMatch(p -> p.doesApply(stack)))
        {
            if (player.canConsume(false)) {
                player.setCurrentHand(hand);
                cir.setReturnValue(new TypedActionResult<ItemStack>(ActionResult.SUCCESS, stack));
            } else {
                cir.setReturnValue(new TypedActionResult<ItemStack>(ActionResult.FAIL, stack));
            }
        }
    }

    @Inject(method = "finishUsing", at = @At("HEAD"), cancellable = true)
    public void getOmnivoreOnItemFinishedUsing(ItemStack stack, World world, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        Optional<EdiblePower> ediblePower = OriginComponent.getPowers(entity, EdiblePower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        if(ediblePower.isPresent())
        {
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                int hunger = ediblePower.get().getHunger();
                float saturation = ediblePower.get().getSaturation();
                player.getHungerManager().add(hunger, saturation);
                world.playSound(null, player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
                player.incrementStat(Stats.USED.getOrCreateStat((Item)(Object)this));
                if (player instanceof ServerPlayerEntity) {
                    Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) player, stack);
                }
            }
            if (stack.getItem() == Items.TNT || stack.getItem() == Items.TNT_MINECART) { // TNT/TNT minecart
                world.createExplosion(null, entity.getBlockPos().getX(), entity.getBlockPos().getY()+1, entity.getBlockPos().getZ(), 1.5f, Explosion.DestructionType.NONE);
            }

            int damage = ediblePower.get().getDamage();;
            if (stack.isDamageable() && damage > 0) stack.damage(damage, entity, (user) -> user.sendToolBreakStatus(entity.getActiveHand()));
            else stack.decrement(1);
            cir.setReturnValue(stack);
        }
    }
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void cachePlayer(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        this.owner = entity;
    }
}
