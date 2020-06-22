package com.github.Crupette.biomechanics.util.network;

import com.github.Crupette.biomechanics.block.entity.Biological;
import com.github.Crupette.biomechanics.block.entity.BiologicalAdjacent;
import com.github.Crupette.biomechanics.block.entity.HeartCaseBlockEntity;
import com.github.Crupette.biomechanics.block.entity.OxygenPumpBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;

public class CirculatoryNetwork {
    public static final int BLOOD_CALORIE_MAX_SATURATION = 50;
    public static final int BLOOD_OXYGEN_MAX_SATURATION = 150;

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
        if(!this.children.contains(entity)) {
            this.children.add(entity);
            if (entity instanceof OxygenPumpBlockEntity) {
                this.lungs.add((OxygenPumpBlockEntity) entity);
            }
            this.calorieStorageCapacity += ((Biological) entity).getCalorieStorageCapacity();
        }
    }

    public void removeChild(BlockEntity entity){
        this.children.remove(entity);
        this.calorieStorageCapacity -= ((Biological)entity).getCalorieStorageCapacity();
    }

    public void reset(){
        this.calorieStorageCapacity = this.heart.getCalorieStorageCapacity();

        this.children.clear();
        this.lungs.clear();
        this.children.add(this.heart);
    }

    public void onBeat(int health){
        this.heartHealth = health;
        this.calorieStorage += (this.bloodCalories / 2);
        this.bloodCalories /= 2;

        //Transfer some calories from fat stores
        if(this.calorieOverflow > 0){
            int transfer = Math.min(this.BLOOD_CALORIE_MAX_SATURATION * this.heartHealth, this.calorieOverflow) - this.bloodCalories;
            if(transfer < 0) return;
            this.bloodCalories += transfer;
            this.calorieOverflow -= transfer;
        }else {
            int transfer = Math.min(this.BLOOD_CALORIE_MAX_SATURATION * this.heartHealth, this.calorieStorage) - this.bloodCalories;
            if(transfer < 0) return;
            this.bloodCalories += transfer;
            this.calorieStorage -= transfer;
        }

        //Make sure no weird underflow happens
        if(this.calorieStorage < 0) this.calorieStorage = 0;

        //Convert damaging fat to normal fat when space is made
        if(this.calorieStorage != this.calorieStorageCapacity && this.calorieOverflow > 0){
            int transfer = Math.min(this.calorieOverflow, this.calorieStorageCapacity - this.calorieStorage);
            this.calorieStorage += transfer;
            this.calorieOverflow -= transfer;
        }

        //Convert excess calories to damaging calories
        if(this.calorieStorage > this.calorieStorageCapacity){
            this.calorieOverflow += this.calorieStorage - this.calorieStorageCapacity;
            this.calorieStorage = this.calorieStorageCapacity;
        }
        if(this.calorieOverflow < 0) this.calorieOverflow = 0;

        this.children.forEach((child) -> {
            ((Biological)child).onBeat();
        });
    }

    public int requestCalories(int cal){
        if(this.bloodCalories < cal){
            int ret = this.bloodCalories;
            this.bloodCalories = 0;

            if(this.calorieStorage < (cal - ret)){
                ret += this.calorieStorage;
                this.calorieStorage = 0;
                if(this.calorieOverflow < (cal - ret)){
                    ret += this.calorieOverflow;
                    this.calorieOverflow = 0;
                }else{
                    this.calorieOverflow -= (cal - ret);
                    ret += (cal - ret);
                }
            }else{
                this.calorieStorage -= (cal - ret);
                ret += (cal - ret);
            }

            //System.out.println("Requesting beat : " + this.heart);
            this.heart.requestBeat();
            return ret;
        }else{
            this.bloodCalories -= cal;
            return cal;
        }
    }

    public int requestOxygen(int oxygen){
        int ret = Math.min(this.bloodOxygen, oxygen);
        this.bloodOxygen -= ret;
        if(this.bloodOxygen <= 0){

            for(OxygenPumpBlockEntity lung : this.lungs){
                //System.out.println("Requesting breath : " + lung);
                lung.requestBreath();
            }
        }
        return ret;
    }

    public int provideCalories(int calories){
        int ret = Math.min(calories, this.BLOOD_CALORIE_MAX_SATURATION * this.heartHealth);
        this.bloodCalories += ret;
        return ret;
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

        networkTag.putInt("heartHealth", this.heartHealth);
        networkTag.putInt("storedCaloriesMaximum", this.calorieStorageCapacity);

        tag.put("circulatorySystem",  networkTag);
    }

    public void fromTag(CompoundTag tag){
        CompoundTag networkTag = (CompoundTag) tag.get("circulatorySystem");
        if(networkTag == null) return;

        this.bloodCalories = networkTag.getInt("bloodCalories");
        this.bloodOxygen = networkTag.getInt("bloodOxygen");
        this.calorieStorage = networkTag.getInt("storedCalories");
        this.calorieOverflow = networkTag.getInt("overflowCalories");

        this.heartHealth = networkTag.getInt("heartHealth");
        this.calorieStorageCapacity = networkTag.getInt("storedCaloriesMaximum");
    }

    public int getCalorieStorage() { return this.calorieStorage; }
    public int getCalorieStorageCapacity() { return this.calorieStorageCapacity; }
    public int getCalorieOverflow() { return this.calorieOverflow; }

    public int getBloodCalories() { return this.bloodCalories; }
    public int getBloodOxygen() { return this.bloodOxygen; }

    public int getHeartHealth() { return this.heartHealth; }

    public void setCalorieStorage           (int val) { this.calorieStorage = val; }
    public void setCalorieStorageCapacity   (int val) { this.calorieStorageCapacity = val; }
    public void setCalorieOverflow          (int val) { this.calorieOverflow = val; }
                                             
    public void setBloodCalories            (int val) { this.bloodCalories = val; }
    public void setBloodOxygen              (int val) { this.bloodOxygen = val; }

    public void setHeartHealth             (int val) { this.heartHealth = val; }
}
