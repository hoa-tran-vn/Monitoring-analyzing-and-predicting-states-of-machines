package com.example.myfinalappsproject;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class MachineClass  implements Serializable, Comparable<MachineClass>{
    private int Id;
    private Double MachinePowerPharseA;
    private Double MachinePowerPharseB;
    private Double MachinePowerPharseC;
    private String MachineName;
    private String MachineOrigin;
    private String MachineDuty;
    private int MachineID;
    private int MachineStatus;
    private int MachineRunTime;
    private int MachineWaitTime;
    private int MachineSetUpTime;
    private int MachineOffTime;
    private Double MachineEnergy;
    private LocalDateTime MachineDateTime;

    public int getMachineStatus() {
        return MachineStatus;
    }

    public MachineClass() {
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getMachineDuty() {
        return MachineDuty;
    }

    public String getMachineOrigin() {
        return MachineOrigin;
    }

    public double getMachineEnergy() {
        return MachineEnergy;
    }

    public LocalDateTime getMachineDateTime() {
        return MachineDateTime;
    }

    public int getMachineRunTime() {
        return MachineRunTime;
    }

    public int getMachineWaitTime() {
        return MachineWaitTime;
    }

    public int getMachineSetUpTime() {
        return MachineSetUpTime;
    }

    public int getMachineOffTime() {
        return MachineOffTime;
    }


    public String getMachineName() {
        return MachineName;
    }

    public int getMachineID() {
        return MachineID;
    }

    public Double getMachinePowerPharseA() {
        return MachinePowerPharseA;
    }

    public Double getMachinePowerPharseB() {
        return MachinePowerPharseB;
    }

    public Double getMachinePowerPharseC() {
        return MachinePowerPharseC;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////


    public void setMachineDuty(String machineDuty) {
        MachineDuty = machineDuty;
    }

    public void setMachineOrigin(String machineOrigin) {
        MachineOrigin = machineOrigin;
    }

    public void setMachineDateTime(LocalDateTime machineDateTime) {
        MachineDateTime = machineDateTime;
    }

    public void setMachineEnergy(Double machineEnergy) {
        MachineEnergy = machineEnergy;
    }

    public void setMachineRunTime(int machineRunTime) {
        MachineRunTime = machineRunTime;
    }

    public void setMachineWaitTime(int machineWaitTime) {
        MachineWaitTime = machineWaitTime;
    }

    public void setMachineSetUpTime(int machineSetUpTime) {
        MachineSetUpTime = machineSetUpTime;
    }

    public void setMachineOffTime(int machineOffTime) {
        MachineOffTime = machineOffTime;
    }

    public void setMachineName(String machineName) {
        MachineName = machineName;
    }

    public void setMachineID(int machineID) {
        MachineID = machineID;
    }

    public void setMachineStatus(int machineStatus) {
        MachineStatus = machineStatus;
    }

    public void setMachinePowerPharseA(Double machinePowerPharseA) {
        MachinePowerPharseA = machinePowerPharseA;
    }

    public void setMachinePowerPharseB(Double machinePowerPharseB) {
        MachinePowerPharseB = machinePowerPharseB;
    }

    public void setMachinePowerPharseC(Double machinePowerPharseC) {
        MachinePowerPharseC = machinePowerPharseC;
    }

    @Override
    public int compareTo(MachineClass machineClass) {
        return (this.MachineDateTime).compareTo(machineClass.MachineDateTime);
    }
}
