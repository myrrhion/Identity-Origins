package myrrhion.identityorigins.powers;


import draylar.identity.cca.IdentityComponent;
import draylar.identity.network.ClientNetworking;
import draylar.identity.registry.Components;
import io.github.apace100.origins.power.ActiveCooldownPower;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.util.HudRender;
import io.netty.buffer.Unpooled;
import myrrhion.identityorigins.IdentityOrigins;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;

public class TransformPower extends ActiveCooldownPower {
    EntityType<LivingEntity> alt_form;
    public TransformPower(PowerType<?> type, PlayerEntity player, int cooldownDuration, HudRender hudRender, EntityType<LivingEntity> enti_type) {
        super(type, player, cooldownDuration, hudRender, null);
        alt_form = enti_type;
    }
    @Override
    public void onUse() {
        if(canUse()) {
            for(int i = 0; i < 32; ++i) {
                player.world.addParticle(ParticleTypes.POOF, player.getParticleX(0.5), player.getRandomBodyY(), player.getParticleZ(0.5),0,0,0);
            }

            if (!player.world.isClient) {
                transform();
            }
                use();
        }
    }



    private void transform(){
        IdentityComponent current = Components.CURRENT_IDENTITY.get(player);
        if(current.getIdentity() == null){
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeIdentifier(Registry.ENTITY_TYPE.getId(alt_form));
            ClientPlayNetworking.send(ClientNetworking.IDENTITY_REQUEST, packet);
        }
        else{
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeIdentifier(Registry.ENTITY_TYPE.getId(EntityType.PLAYER));
            ClientPlayNetworking.send(ClientNetworking.IDENTITY_REQUEST, packet);
        }

    }
}
