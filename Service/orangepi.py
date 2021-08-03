import serial
import time
import os
import datetime
import pymssql
"""This function is used to get the node ID then return the code of monitoring equipment.
This code was saved in database"""
def getId(package):
	zigbeeID = package[0:5]
	return zigbeeID
"""This group return the parameter of phase A by analyze the package receive"""
def getphaseAvoltage(package):
	phaseAvoltage = package[5:8]
	return phaseAvoltage
def getphaseAcurent(package):
	phaseAcurrent = package[8:11]
	return float(phaseAcurrent)/10
def getphaseApower(package):
	phaseApower = package[11:15]
	return phaseApower
"""This group return the parameter of phase B by analyze the package receive"""
def getphaseBvoltage(package):
	phaseBvoltage = package[15:18]
	return phaseBvoltage
def getphaseBcurent(package):
	phaseBcurrent = package[18:21]
	return float(phaseBcurrent)/10
def getphaseBpower(package):
	phaseBpower = package[21:25]
	return phaseBpower
"""This group return the parameter of phase C by analyze the package receive"""
def getphaseCvoltage(package):
	phaseCvoltage = package[25:28]
	return phaseCvoltage
def getphaseCcurent(package):
	phaseCcurrent = package[28:31]
	return float(phaseCcurrent)/10
def getphaseCpower(package):
	phaseCpower = package[31:35]
	return phaseCpower
"""This function is get the state of the machine"""
def gestateofMachine(package):
	state = package[35]
	return state
"""This function is used to get machine monitor by node"""
def searchforMachine(NodeID):
	IdMachine = None
	"""Get Machine Id"""
	cursor.execute("SELECT GiamSatMay FROM BoGiamSat WHERE MaBoGiamSat = %s",(NodeID))
	rows = cursor.fetchall()
	if len(rows) >= 1 :
		IdMachine = rows[0][0]
	return  IdMachine
"""This function is use to update state of machine"""
def updateMachineStatus(phaseApower, phaseBpower, phaseCpower, NodeID, State):
	# get id of machine that want to update
	IdMachine = searchforMachine(NodeID)
	if IdMachine != None:
		power = phaseApower + phaseBpower + phaseCpower
		# check IdMachine exist?
		cursor.execute("SELECT COUNT(*) FROM TinhTrangMay WHERE May = %s", IdMachine)
		rows = cursor.fetchall()
		count = rows[0][0]
		if count == 0:
			cursor.execute("INSERT TinhTrangMay (BoGiamSat, may, trangThai, CongSuat , ThoiGianCapNhatCuoiCung, HinhAnh , ThoiGianMay) VALUES (%s,%s,%s,%s,GETDATE(),NULL,NULL)", (NodeID, IdMachine, State, power))
		else: 
			cursor.execute("UPDATE TinhTrangMay SET trangThai = %S, CongSuat = %s, ThoiGianCapNhatCuoiCung = GETDATE() WHERE may = %s", (state, power, IdMachine))
"""This function is used to capture history of machine"""
def captureMachineStatus(phaseApower, phaseAcurrent, phaseAvoltage, phaseBpower, phaseBcurrent, phaseBoltage, phaseCpower, phaseCcurrent, phaseCvoltage, NodeID, State, lightstate):
	# get infor of machine
	IdMachine = searchforMachine(NodeID)
	# insert new row
	if IdMachine != None:
		# return state of machine	
		#checkOff(IdMachine)
		stateoflight = int(State)
		stateofmachine = determinestateofMachine(stateoflight,phaseAcurrent, phaseBcurrent, phaseCcurrent, IdMachine)
		if IdMachine == 252:
			stateofmachine = 5
		cursor.execute("INSERT into LichSuMay (GiamSatMay,CongSuatPhaA,DongDienPhaA,DienApPhaA,CongSuatPhaB,DongDienPhaB,DienApPhaB,CongSuatPhaC,DongDienPhaC,DienApPhaC,trangThai,Chuoitruyen) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",(IdMachine,phaseApower, phaseAcurrent, phaseAvoltage, phaseBpower, phaseBcurrent, phaseBoltage, phaseCpower, phaseCcurrent, phaseCvoltage,stateofmachine,lightstate))
		#conn.commit()
def determinestateofMachine(state,phaseAcurrent, phaseBcurrent, phaseCcurrent, IdMachine):
	if(state == 0 | state == 1 | state == 4 | state == 5):
		state = 5
	else:
		state = 3	
		current = phaseAcurrent + phaseBcurrent + phaseCcurrent
		cursor.execute("SELECT Inactive, Standby FROM Mays WHERE Id = %s",IdMachine)
		rows = cursor.fetchall()
		if len(rows) >= 1 :
			Inactive = rows[0][0]
			Standby = rows[0][1]
			if current < Inactive:
				state = 2
			elif current > Standby:
				state = 4
				cursor.execute("UPDATE LichSuMay SET trangThai = 4 WHERE GiamSatMay = %s AND trangThai = 3 AND ThoiGianCapNhat >= DATEADD(MINUTE,-5,GETDATE())",IdMachine)
	return state
def checkOff(IdMachine):
	#Get time and sate of previous data of IdMachine in current date
	cursor.execute("SELECT ThoiGianCapNhat, trangThai FROM LichSuMay WHERE Id = (SELECT MAX(Id) FROM LichSuMay WHERE GiamSatMay = %s) AND DAY(ThoiGianCapNhat) = DAY(GETDATE()) AND MONTH(ThoiGianCapNhat) = MONTH(GETDATE()) AND YEAR(ThoiGianCapNhat) = YEAR(GETDATE()) ", IdMachine)
	rows = cursor.fetchall()
	if len(rows) >= 1 :
		previousTime = rows[0][0]
		previousState = rows[0][1]
		#Skip if the machine in error situation 
		if(previousState != 6):
			cursor.execute("SELECT GETDATE()")
			rows = cursor.fetchall()
			if len(rows) >= 1 :
				currentTime = rows[0][0]
			#Skip if previousTime is None
			if(previousTime != None):
				timeDelta = currentTime - previousTime
				timeDelta = (int)(timeDelta.total_seconds())
				#Skip if time delta < 5 minutes
				if(timeDelta > 300):
					#Insert 2 off samples that is off start time and off finish time 
					cursor.execute("INSERT LichSuMay (GiamSatMay,ThoiGianCapNhat, CongSuatPhaA,DongDienPhaA,DienApPhaA,CongSuatPhaB,DongDienPhaB,DienApPhaB,CongSuatPhaC,DongDienPhaC,DienApPhaC,trangThai) VALUES (%s,DATEADD(MINUTE,1,%s),0,0,0,0,0,0,0,0,0,1)", (IdMachine, previousTime))
					cursor.execute("INSERT LichSuMay (GiamSatMay,ThoiGianCapNhat, CongSuatPhaA,DongDienPhaA,DienApPhaA,CongSuatPhaB,DongDienPhaB,DienApPhaB,CongSuatPhaC,DongDienPhaC,DienApPhaC,trangThai) VALUES (%s,DATEADD(MINUTE,-1,%s),0,0,0,0,0,0,0,0,0,1)", (IdMachine, currentTime)) 
"""Main function"""
ser = serial.Serial('/dev/ttyS1',38400,timeout=300)
conn=pymssql.connect(server='192.168.100.254\ONEDUYKHANH', user='sa ',password='thank0291', database='OneDuyKhanh4', autocommit=True)
cursor = conn.cursor()
while 1:
	try:	
		line=0
		time.sleep(0.1)
		line = ser.readline()
		if line == 0 :
			continue	
		if len(line) < 37 :
			continue
		package = line#[(len(line)-37):(len(line)-1)].decode("utf-8")
		# return Node ID 
		node  = getId(package)
		if node == "00140": #Bo do cong suat tong
			phaseAvoltage = 225.0
			phaseAcurrent = 10*getphaseAcurent(package)
			phaseApower = phaseAvoltage*phaseAcurrent
			phaseBvoltage = 225.0
			phaseBcurrent = 10*getphaseBcurent(package)
			phaseBpower = phaseBvoltage*phaseBcurrent
			phaseCvoltage = 225.0
			phaseCcurrent = 10*getphaseCcurent(package)
			phaseCpower = phaseCvoltage*phaseCcurrent
			state = gestateofMachine(package)
			lightstate = gestateofMachine(package)
		else:
		# Analyze package to get information
			phaseAvoltage = getphaseAvoltage(package)
			phaseAcurrent = getphaseAcurent(package)
			phaseApower = getphaseApower(package)
			phaseBvoltage = getphaseBvoltage(package)
			phaseBcurrent = getphaseBcurent(package)
			phaseBpower = getphaseBpower(package)
			phaseCvoltage = getphaseCvoltage(package)	
			phaseCcurrent = getphaseCcurent(package)
			phaseCpower = getphaseCpower(package)
			state = gestateofMachine(package)
			lightstate = gestateofMachine(package)
		captureMachineStatus(phaseApower,phaseAcurrent,phaseAvoltage,phaseBpower,phaseBcurrent,phaseBvoltage,phaseCpower,phaseCcurrent,phaseCvoltage,node,state,lightstate)
		updateMachineStatus(phaseApower, phaseBpower, phaseCpower, node, State)
	except Exception as e:
			print(e)	
