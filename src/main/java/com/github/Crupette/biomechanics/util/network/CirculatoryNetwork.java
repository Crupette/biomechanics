package com.github.Crupette.biomechanics.util.network;

import com.github.Crupette.biomechanics.block.entity.Biological;
import com.github.Crupette.biomechanics.block.entity.HeartCaseBlockEntity;
import com.github.Crupette.biomechanics.block.entity.OxygenPumpBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class CirculatoryNetwork {
    public static final int BLOOD_CALORIE_MAX_SATURATION = 50;
    public static final int BLOOD_OXYGEN_MAX_SATURATION = 100;

    private final List<BlockEntity> children = new ArrayList<>();
    private List<OxygenPumpBlockEntity> lungs = new ArrayList<>();
    private final HeartCaseBlockEntity heart;

    private int bloodCalories = 0;
    private int bloodOxygen = 0;
    private int heartHealth = 0;

    private int calorieStorage = 0;
    private int calorieOverflow = 0;
    private int calorieStorageCapacity = 0;

    public CirculatoryNetwork(HeartCaseBlockEntity heart){
        this.heart = heart;
    }

    public void addChild(BlockEntity entity){
        this.children.add(entity);
        if(entity instanceof OxygenPumpBlockEntity){
            this.lungs.add((OxygenPumpBlockEntity) entity);
        }
        this.calorieStorageCapacity += ((Biological)entity).getCalorieStorageCapacity();
    }

    public void removeChild(BlockEntity entity){
        this.children.remove(entity);
        this.calorieStorageCapacity -= ((Biological)entity).getCalorieStorageCapacity();
    }

    public void reset(){
        this.children.forEach((child) -> {
            if(child != heart){
                this.calorieStorageCapacity -= ((Biological)child).getCalorieStorageCapacity();
            }
        });
        this.children.clear();
        this.children.add(this.heart);
    }

    public void onBeat(int health){
        this.heartHealth = health;
        this.calorieStorage += this.bloodCalories / 2;
        this.bloodCalories /= 2;

        //Testing
        this.bloodCalories = 100;
        this.calorieStorage = 32;

        if(this.calorieStorage > this.calorieStorageCapacity){
            this.calorieOverflow = this.calorieStorage - this.calorieStorageCapacity;
            this.calorieStorage = this.calorieStorageCapacity;
        }
        if(this.calorieStorage < this.calorieStorageCapacity && this.calorieOverflow > 0){
            int transfer = Math.min(this.calorieStorageCapacity - this.calorieStorage, this.calorieOverflow);
            this.calorieStorage += transfer;
        }

        this.children.forEach((child) -> {
            ((Biological)child).onBeat();
        });
    }

    public int requestCalories(int cal){
        int ret = Math.min(this.bloodCalories, cal);
        this.bloodCalories -= ret;
        if(this.bloodCalories <= 0){
            int storedObtained = Math.min(this.calorieStorage, cal - ret);
            this.calorieStorage -= storedObtained;
            ret += storedObtained;

            this.heart.requestBeat();
        }

        return ret;
    }

    public int requestOxygen(int oxygen){
        int ret = Math.min(this.bloodOxygen, oxygen);
        this.bloodOxygen -= ret;
        if(this.bloodOxygen <= 0){

            for(OxygenPumpBlockEntity lung : this.lungs){
                lung.requestBreath();
            }
        }
        return ret;
    }

    public int provideCalories(int calories){
        this.bloodCalories += calories;
        if(this.bloodCalories > (BLOOD_CALORIE_MAX_SATURATION * heartHealth)){
            this.calorieStorage += (BLOOD_CALORIE_MAX_SATURATION * heartHealth) - this.bloodCalories;
            this.bloodCalories = (BLOOD_CALORIE_MAX_SATURATION * heartHealth);
        }
        return calories;
    }

    public int provideOxygen(int oxygen) {
        if(this.bloodOxygen + oxygen > (BLOOD_OXYGEN_MAX_SATURATION * heartHealth)){
            int dif = (BLOOD_CALORIE_MAX_SATURATION * heartHealth) - this.bloodOxygen;
            this.bloodOxygen += dif;
            return dif;
        }else{
            this.bloodOxygen += oxygen;
            return oxygen;
        }
    }

    public void toTag(CompoundTag tag){
        CompoundTag networkTag = new CompoundTag();
        networkTag.putInt("bloodCalories", this.bloodCalories);
        networkTag.putInt("bloodOxygen", this.bloodOxygen);
        networkTag.putInt("storedCalories", this.calorieStorage);
        networkTag.putInt("overflowCalories", this.calorieOverflow);

        tag.put("circulatorySystem",  networkTag);
    }

    public void fromTag(CompoundTag tag){
        CompoundTag networkTag = (CompoundTag) tag.get("circulatorySystem");
        if(networkTag == null) return;

        this.bloodCalories = networkTag.getInt("bloodCalories");
        this.bloodOxygen = networkTag.getInt("bloodOxygen");
        this.calorieStorage = networkTag.getInt("storedCalories");
        this.calorieOverflow = networkTag.getInt("overflowCalories");
    }
}
