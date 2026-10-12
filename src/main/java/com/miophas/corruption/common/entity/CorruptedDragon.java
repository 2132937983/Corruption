package com.miophas.corruption.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.PartEntity;

/** A remnant with separately selectable dragon hitboxes, allocated before the spawn packet. */
public final class CorruptedDragon extends CorruptedRemnant {
    private final DragonPart[] parts;
    public CorruptedDragon(EntityType<? extends CorruptedDragon> type, Level level) {
        super(type,level);
        parts=new DragonPart[]{new DragonPart(this,0,1.25F,1.25F),new DragonPart(this,1,3,2),new DragonPart(this,2,3,3),
                new DragonPart(this,3,2,2),new DragonPart(this,4,2,2),new DragonPart(this,5,2,2),new DragonPart(this,6,4,2),new DragonPart(this,7,4,2)};
        setId(ENTITY_COUNTER.getAndAdd(parts.length+1)+1);noCulling=true;
        CompoundTag appearance=new CompoundTag();appearance.putString("Type","minecraft:ender_dragon");
        appearance.putFloat("Width",16);appearance.putFloat("Height",8);appearance.putDouble("AttackDamage",10);
        appearance.put("Entity",new CompoundTag());initialize(appearance,200);
    }
    @Override public void setId(int id) { super.setId(id);if(parts!=null)for(int i=0;i<parts.length;i++)parts[i].setId(id+i+1); }
    @Override public boolean isMultipartEntity() { return true; }
    @Override public PartEntity<?>[] getParts() { return parts; }
    @Override public boolean isPickable() { return false; }
    void updateParts() {
        for(int i=0;i<parts.length;i++) {
            var p=parts[i];double x=p.getX(),y=p.getY(),z=p.getZ();p.setPos(dragon().partPosition(i));
            p.xo=x;p.yo=y;p.zo=z;p.xOld=x;p.yOld=y;p.zOld=z;
        }
    }
    public static final class DragonPart extends PartEntity<CorruptedDragon> {
        private final int index;
        private final EntityDimensions size;
        DragonPart(CorruptedDragon parent,int index,float width,float height) { super(parent);this.index=index;size=EntityDimensions.scalable(width,height);refreshDimensions(); }
        @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
        @Override protected void readAdditionalSaveData(CompoundTag tag) {}
        @Override protected void addAdditionalSaveData(CompoundTag tag) {}
        @Override public boolean shouldBeSaved() { return false; }
        @Override public boolean isPickable() { return true; }
        @Override public boolean is(Entity entity) { return this==entity||getParent()==entity; }
        @Override public EntityDimensions getDimensions(Pose pose) { return size==null?super.getDimensions(pose):size; }
        @Override public boolean hurt(DamageSource source,float amount) { return getParent().hurtDragonPart(index==0,source,amount); }
    }
}
