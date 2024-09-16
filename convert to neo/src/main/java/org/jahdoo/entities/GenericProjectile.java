package org.jahdoo.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.AbstractEntityProperty;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.ProjectileProperties;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;

public class GenericProjectile extends ProjectileProperties {

    private String projectileSelectionIndex;
    private DefaultEntityBehaviour getProjectile;
    WandAbilityHolder wandAbilityHolder;
    private AbstractElement getElement;
    private int customDiscardTime;

    public GenericProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GenericProjectile(
        Player player,
        double offset,
        String projectileSelectionIndex
    ) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), player.level());
        this.setProjectileWithOffsets(this, player, offset, 2);
        this.reapplyPosition();
        this.setOwner(player);
//        compoundTag = player.getMainHandItem().getTag();
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.getProjectile = ProjectilePropertyRegister.REGISTRY.get(GeneralHelpers.modResourceLocation(projectileSelectionIndex)).getEntityProperty();
        this.getProjectile.getGenericProjectile(this);
    }

    public WandAbilityHolder getCompoundTag(){
        return wandAbilityHolder;
    }

    public WandAbilityHolder wandAbilityHolder(){
        return this.wandAbilityHolder;
    }

    public GenericProjectile(
        Player player,
        double offset,
        String projectileSelectionIndex,
        WandAbilityHolder wandAbilityHolder,
        double distance
    ) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), player.level());
        this.setProjectileWithOffsets(this, player, offset, distance);
        this.reapplyPosition();
        this.setOwner(player);
        this.wandAbilityHolder = wandAbilityHolder;
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.getProjectile = ProjectilePropertyRegister.REGISTRY.get(GeneralHelpers.modResourceLocation(projectileSelectionIndex)).getEntityProperty();
        this.getProjectile.getGenericProjectile(this);
    }

    public GenericProjectile(Entity owner, double spawnX, double spawnY, double spawnZ, String projectileSelectionIndex, WandAbilityHolder wandAbilityHolder, AbstractElement abstractElement) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), owner.level());
        this.moveTo(spawnX, spawnY, spawnZ, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        this.setOwner(owner);
        this.wandAbilityHolder = wandAbilityHolder;
        this.getElement = abstractElement;
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.getProjectile = ProjectilePropertyRegister.REGISTRY.get(GeneralHelpers.modResourceLocation(projectileSelectionIndex)).getEntityProperty();
        this.getProjectile.getGenericProjectile(this);
    }

    public GenericProjectile(Entity owner, double spawnX, double spawnY, double spawnZ, String projectileSelectionIndex, WandAbilityHolder wandAbilityHolder) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), owner.level());
        this.moveTo(spawnX, spawnY, spawnZ, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        this.setOwner(owner);
        this.wandAbilityHolder = wandAbilityHolder;
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.getProjectile = ProjectilePropertyRegister.REGISTRY.get(GeneralHelpers.modResourceLocation(projectileSelectionIndex)).getEntityProperty();
        this.getProjectile.getGenericProjectile(this);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if(entity instanceof Projectile || entity instanceof Player || entity instanceof EternalWizard) return;
        if(!(entityHitResult.getEntity() instanceof LivingEntity livingEntity)) return;
        if(this.getProjectile != null) {
            this.getProjectile.onEntityHit(livingEntity);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        if(this.getProjectile != null){
            this.getProjectile.onBlockBlockHit(blockHitResult);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.customDiscardTime > 0) if(this.tickCount > this.customDiscardTime) this.discard();

        if(getProjectile != null){
            this.getProjectile.onTickMethod();
            this.getProjectile.discardCondition();
        }
    }

    @Override
    public AbstractElement getElementType() {
        return this.getElement;
    }

    public void setCustomDiscardTime(int customDiscardTime){
        this.customDiscardTime = customDiscardTime;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("projectileIndex", this.projectileSelectionIndex);
        if(this.getElement != null){
            pCompound.putInt("elementId", this.getElement.getTypeId());
        }
//        pCompound.put("compoundTag", compoundTag);
        if(this.getProjectile != null){
            getProjectile.addAdditionalDetails(pCompound);
        }
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.projectileSelectionIndex = pCompound.getString("projectileIndex");
//        this.wandAbilityHolder.abilityProperties().forEach(
//            (s, wandAbilityHolder) -> {
//                wandAbilityHolder.abilityProperties()
//                wandAbilityHolder.abilityProperties().forEach(
//                    (s1, ability) ->{
//                        pCompound.
//                    }
//                );
//            }
//        );
//        this.compoundTag = pCompound.getCompound("compoundTag");

        if(this.getElement == null && pCompound.getInt("elementId") > 0){
            this.getElement = ElementRegistry.getElementByTypeId(pCompound.getInt("elementId")).get(0);
        }

        AbstractEntityProperty abstractProjectileProperty = ProjectilePropertyRegister.REGISTRY.get(
            GeneralHelpers.modResourceLocation(pCompound.getString("projectileIndex"))
        );

        if(abstractProjectileProperty != null) {
            this.getProjectile = abstractProjectileProperty.getEntityProperty();
            this.getProjectile.readCompoundTag(pCompound);
            this.getProjectile.getGenericProjectile(this);
        }
    }

}
