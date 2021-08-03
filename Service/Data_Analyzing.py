import pyodbc 
import time, datetime
from datetime import timedelta
import dateutil.relativedelta
import calendar

RUN_LOWBOUND = datetime.time(hour=7, minute = 54)     
RUN_UPPERBOUND = datetime.time(hour=0) 

def Should_run():
    # Get the current time
    curentTime = datetime.datetime.now().time()
    # Compare current time to run bounds
    lowBound = RUN_LOWBOUND <= curentTime
    upperBound = RUN_UPPERBOUND >= curentTime
    # If the bounds wrap the 24-hour day, use a different check logic
    if RUN_LOWBOUND > RUN_UPPERBOUND:
        return lowBound or upperBound
    else:
        return lowBound and upperBound
def Get_wait_seconds():
    # Get the current datetime
    curentTime = datetime.datetime.now()
    # Create a datetime with *today's* RUN_LOWBOUND
    lowDatetime = datetime.datetime.combine(datetime.date.today(), RUN_LOWBOUND)
    # Create a timedelta for the time until *today's* RUN_LOWBOUND
    timeDelta = lowDatetime - curentTime
    # Ignore timedelta days (may be negative), return timedelta.seconds (always positive)
    return (timeDelta.seconds)
def SQLconection():
    conection = pyodbc.connect("Driver={SQL Server Native Client 11.0};"               
                               "Server=DESKTOP-AQ3PPMN\SQLEXPRESS;"
                               "Database=OneDuyKhanh4;"
                               "username=Hoa;"
                               "password=123456;"
                               "Trusted_Connection=yes;")
    return conection
def MinIdOfCurentDate():
    #Get min Id of data of current date
    cursor.execute('SELECT MIN(Id) FROM LichSuMay WHERE CONVERT(DATE, ThoiGianCapNhat) = CONVERT(DATE, GETDATE())')
    rows = cursor.fetchall()
    idMin = rows[0][0]
    if(idMin == None):
        cursor.execute("SELECT MAX(Id) FROM LichSuMay")
        rows = cursor.fetchall()
        idMin = rows[0][0]
        if idMin != None:
            idMin += 1
    return idMin
def MaxIdOfCurentDate():
    #Get max Id of data of current date
    cursor.execute('SELECT MAX(Id) FROM LichSuMay WHERE CONVERT(DATE, ThoiGianCapNhat) = CONVERT(DATE, GETDATE())')
    rows = cursor.fetchall()
    idMax = rows[0][0]
    return idMax
def GetData(id):
    #Get data base on Id
    cursor.execute("SELECT GiamSatMay, ThoiGianCapNhat, CongSuatPhaA, CongSuatPhaB, CongSuatPhaC, trangThai FROM LichSuMay WHERE Id = ?", id)
    rows = cursor.fetchall()
    data = []
    if rows != []:
        for tem in range(0, len(rows[0])):
            data.append(rows[0][tem])
    return data
def CalculateData(previousData, data):
    if(previousData[5] != 6):
        #temResultData: (RunningTime, WaitTime, BreakDownTime, SetUpTime, OffTime, congsuatRunning=NangLuongRunning, TongNangLuong, ReadyTime)
        temResultData = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
        #Time - previousTime
        temTime = data[1] - previousData[1]
        temTime = (float)(temTime.total_seconds()/60)
        if(temTime >= 5):
            temResultData[4] = temTime
            return temResultData
        #if previousSate = Sate
        elif(previousData[5] == data[5]):
            #Off
            if(data[5] == 1):
                temResultData[4] = temTime
            #Wait
            elif(data[5] == 2):
                temResultData[1] = temTime
            #Ready
            elif(data[5] == 3):
                temResultData[7] = temTime
            #Setup
            elif(data[5] == 4):
                temResultData[3] = temTime
            #Run
            elif(data[5] == 5):
                temResultData[0] = temTime
                #Total Engery of Running
                temResultData[5] = (previousData[2] + previousData[3] + previousData[4] + data[2] + data[3] + data[4])*temTime/2
            #Total Engery
            temResultData[6] = (previousData[2] + previousData[3] + previousData[4] + data[2] + data[3] + data[4])*temTime/2
            return temResultData
        else:
            return 0
    else:
        return 0 
#Delete data when state of current data same state of previous data and powerDelta <= 0.1*currentData
def OptimizeData(id, previousData, data):
    powerCurrent = data[2] + data[3] + data[4]
    powerDelta = abs(powerCurrent - (previousData[2] + previousData[3] + previousData[4]))
    print(powerDelta, powerCurrent)
    if(powerDelta <= 0.1*powerCurrent):
        cursor.execute('DELETE LichSuMay WHERE Id = ?', id)
    return 0
def InsertDataOfDay(resultData):
    for idMachine in resultData:
        #Convert type of time (float) to int
        runningTime = (int)(resultData[idMachine][0])
        waitTime = (int)(resultData[idMachine][1])
        breakDownTime = (int)(resultData[idMachine][2])
        setUpTime = (int)(resultData[idMachine][3])
        offTime = (int)(resultData[idMachine][4])
        readyTime = (int)(resultData[idMachine][7])
        #Convert energy of running to power of running
        power = 0
        if(runningTime != 0):
            power = resultData[idMachine][5]/resultData[idMachine][0]
        #Covert unit of energy from W.minute to KWh
        energy = resultData[idMachine][6]/(60*1000) 
        #Check data of current day, it can exist when server restart 
        cursor.execute('SELECT COUNT(*) FROM ThoiGianMay WHERE may = ? AND CONVERT(DATE, NgayCapNhat) = CONVERT(DATE, GETDATE())', idMachine)
        rows = cursor.fetchall()
        count = rows[0][0]
        if count > 0:
            cursor.execute("""UPDATE ThoiGianMay SET NgayCapNhat = GETDATE(), RunningTime = ?, WaitTime= ?, BreakDownTime = ?, SetUpTime = ?, OffTime = ?, congsuat = ?, NangLuong = ?, ReadyTime = ? WHERE may = ? 
                    AND CONVERT(DATE, NgayCapNhat) = CONVERT(DATE, GETDATE())""",runningTime, waitTime, breakDownTime, setUpTime, offTime, power, energy, readyTime, idMachine)
        else:
            cursor.execute("""INSERT INTO ThoiGianMay (NgayCapNhat, may, boGiamSat, Trangthaimay, RunningTime, WaitTime, BreakDownTime, SetUpTime, OffTime, congsuat, NangLuong, ReadyTime) 
                    VALUES (GETDATE(), ?, NULL, NULL, ?, ?, ?, ?, ?, ?, ?, ?)""", idMachine, runningTime, waitTime, breakDownTime, setUpTime, offTime, power, energy, readyTime)
        conection.commit()
    return 0
def UpdateDataOfMonth(resultData):
    for idMachine in resultData:
        #Calculate data of current month
        cursor.execute('''SELECT SUM(RunningTime),
                                 SUM(WaitTime),
                                 SUM(BreakDownTime),
                                 SUM(SetUpTime),
                                 SUM(OffTime),
                                 AVG(congsuat),
                                 SUM(NangLuong),
                                 SUM(ReadyTime) FROM ThoiGianMay WHERE may = ? AND MONTH(NgayCapNhat) = MONTH(GETDATE()) AND YEAR(NgayCapNhat) = YEAR(GETDATE())''', idMachine)
        rows = cursor.fetchall()
        if len(rows) >= 1 :
            RunningTime = rows[0][0]
            WaitTime = rows[0][1]
            BreakDownTime = rows[0][2]
            SetUpTime = rows[0][3]
            OffTime = rows[0][4]
            congsuat = rows[0][5]
            NangLuong = rows[0][6]
            ReadyTime = rows[0][7]
        #Check to insert for new month
        cursor.execute('SELECT COUNT(*) FROM ThoiGianMay_Thang WHERE may = ? AND MONTH(NgayCapNhat) = MONTH(GETDATE()) AND YEAR(NgayCapNhat) = YEAR(GETDATE())', idMachine)
        rows = cursor.fetchall()
        if len(rows) >= 1 :
            count = rows[0][0]
            if count > 0:
                cursor.execute('''UPDATE ThoiGianMay_Thang SET RunningTime = ?, WaitTime = ?, BreakDownTime = ?, SetUpTime = ?, OffTime = ?,
                                congsuat = ?, NangLuong = ?, ReadyTime = ? WHERE may = ? AND MONTH(NgayCapNhat) = MONTH(GETDATE()) 
                                AND YEAR(NgayCapNhat) = YEAR(GETDATE())''', RunningTime, WaitTime, BreakDownTime, SetUpTime, OffTime, congsuat, NangLuong, ReadyTime, idMachine)
            else:
                cursor.execute("""INSERT INTO ThoiGianMay_Thang (NgayCapNhat, may, boGiamSat, RunningTime, WaitTime, BreakDownTime, SetUpTime, OffTime, congsuat, NangLuong, ReadyTime) 
                        VALUES (GETDATE(), ?, NULL, ?, ?, ?, ?, ?, ?, ?, ?)""", idMachine, RunningTime, WaitTime, BreakDownTime, SetUpTime, OffTime, congsuat, NangLuong, ReadyTime)
            conection.commit()
def LoadBreakMachineId():
    cursor.execute("SELECT May FROM SuaChuas WHERE NgayHoanThanh IS NULL")
    rows = cursor.fetchall()
    breakMachineId = []
    if rows != []:
        for tem in range(0, len(rows)):
            breakMachineId.append(rows[tem][0])
    return breakMachineId
def LoadbreakData():
    #Load data of machines that are break or have been repaired current date 
    cursor.execute("SELECT May, NgayYeuCau, NgayHoanThanh FROM SuaChuas WHERE (NgayHoanThanh IS NULL) OR (NgayHoanThanh IS NOT NULL AND CONVERT(DATE, NgayHoanThanh) = CONVERT(DATE, GETDATE()))")
    breakData = cursor.fetchall()
    return breakData
def Handlerbreak(breakData):
    #breakTime: {May: breakTime}
    breakTime = {}
    for count in range(0, len(breakData)):
        #NgayHoanThanh have a value
        if breakData[count][2] != None:
            #NgayYeuCau = current date
            if (breakData[count][1]).date() == datetime.date.today():
                tembreakTime = breakData[count][2] - breakData[count][1]
                tembreakTime = (float)(tembreakTime.total_seconds()/60)
            else:
                tembreakTime = breakData[count][2].time()
                tembreakTime = tembreakTime.second/60 + tembreakTime.minute + tembreakTime.hour*60
        #NgayHoanThanh = Null
        else:
            #NgayYeuCau = current date
            if (breakData[count][1]).date() == datetime.date.today():
                tembreakTime = breakData[count][1].time()
                tembreakTime = 24*60 - (tembreakTime.second/60 + tembreakTime.minute + tembreakTime.hour*60)
            else:    
                tembreakTime = 1440
        #In many break situation at one machine
        if(breakData[count][0] in breakTime):
            breakTime[breakData[count][0]] += tembreakTime
        #In one break situation at one machine
        else:
            breakTime[breakData[count][0]] = tembreakTime
    return breakTime
def checkOff(previousData):
    # Get the current time
    curentTime = datetime.datetime.now()
    for idMachine in previousData:
        if((previousData[idMachine][5] != 1) & (previousData[idMachine][5] != 6)):
            timeDelta = curentTime - previousData[idMachine][1]
            timeDelta = (int)(timeDelta.total_seconds())
            if(timeDelta >= 300):
                cursor.execute("UPDATE TinhTrangMay SET trangThai = 1, ThoiGianCapNhatCuoiCung = GETDATE() WHERE may = ?", idMachine)
                conection.commit()
                previousData[idMachine][5] = 1
                previousData[idMachine][1] = previousData[idMachine][1] + datetime.timedelta(minutes=1)
    return 0
def UpdateStateMachine():
    cursor.execute("UPDATE TinhTrangMay SET trangThai = 1 WHERE trangThai != 1 AND trangThai != 6")
    conection.commit()
    return 0
def RunTimeComment():
    currentDate = datetime.date.today()
    currentDay = currentDate.day
    currentDayName = currentDate.strftime("%A")
    currentMonth = currentDate.month
    currentYear = currentDate.year 

    nextDate = currentDate + timedelta(days = 1)
    #Year
    if((currentDay == 31) & (currentMonth == 12)):
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE YEAR(NgayCapNhat) = YEAR(GETDATE())")
        rows = cursor.fetchall()
        runTimeTotal = rows[0][0]
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE YEAR(NgayCapNhat) = YEAR(GETDATE()) - 1")
        rows = cursor.fetchall()
        previousRunTimeTotal = rows[0][0]
        if(runTimeTotal != None) & (previousRunTimeTotal != None):
            if(previousRunTimeTotal != 0):
                if(runTimeTotal > previousRunTimeTotal):
                    runTimeDelta = runTimeTotal - previousRunTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy năm này tăng %f giờ, tăng %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 5, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    runTimeDelta = previousRunTimeTotal - runTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy năm này giảm %f giờ, giảm %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 5, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ chạy năm này là %f giờ' %(runTimeTotal/60)
                data = [content , 5, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ chạy năm này là %f giờ' %(runTimeTotal/60)
            data = [content , 5, 0, 'MC']
            InsertCommentAndWarning(data)
    #Quarter of the year
    if(((currentDay == 31) & (currentMonth == 3)) | ((currentDay == 30) & (currentMonth == 6)) | ((currentDay == 30) & (currentMonth == 9)) | ((currentDay == 31) & (currentMonth == 12))):
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE MONTH(NgayCapNhat) >= MONTH(GETDATE()) - 2 AND YEAR(NgayCapNhat) = YEAR(GETDATE())")
        rows = cursor.fetchall()
        runTimeTotal = rows[0][0]
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(MONTH,-5,GETDATE()) - DAY(GETDATE()) AND NgayCapNhat <= DATEADD(MONTH,-2,GETDATE()) - DAY(GETDATE())")
        rows = cursor.fetchall()
        previousRunTimeTotal = rows[0][0]
        if(runTimeTotal != None) & (previousRunTimeTotal != None):
            if(previousRunTimeTotal != 0):
                if(runTimeTotal > previousRunTimeTotal):
                    runTimeDelta = runTimeTotal - previousRunTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy quý này tăng %f giờ, tăng %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 4, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    runTimeDelta = previousRunTimeTotal - runTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy quý này giảm %f giờ, giảm %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 4, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ chạy quý này là %f giờ' %(runTimeTotal/60)
                data = [content , 4, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ chạy quý này là %f giờ' %(runTimeTotal/60)
            data = [content , 4, 0, 'MC']
            InsertCommentAndWarning(data)
    #Month
    if(currentMonth != nextDate.month):
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE MONTH(NgayCapNhat) = MONTH(GETDATE()) AND YEAR(NgayCapNhat) = YEAR(GETDATE())")
        rows = cursor.fetchall()
        runTimeTotal = rows[0][0]
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(Month,-1,GETDATE()) - DAY(GETDATE())")
        rows = cursor.fetchall()
        previousRunTimeTotal = rows[0][0]
        if(runTimeTotal != None) & (previousRunTimeTotal != None):
            if(previousRunTimeTotal != 0):
                if(runTimeTotal > previousRunTimeTotal):
                    runTimeDelta = runTimeTotal - previousRunTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy tháng này tăng %f giờ, tăng %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 3, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    runTimeDelta = previousRunTimeTotal - runTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy tháng này giảm %f giờ, giảm %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 3, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ chạy tháng này là %f giờ' %(runTimeTotal/60)
                data = [content , 3, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ chạy tháng này là %f giờ' %(runTimeTotal/60)
            data = [content , 3, 0, 'MC']
            InsertCommentAndWarning(data)
    #Week
    if(currentDayName == 'Sunday'):
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(day, -7, GETDATE())")
        rows = cursor.fetchall()
        runTimeTotal = rows[0][0]
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(day, -14, GETDATE()) AND NgayCapNhat <= DATEADD(day, -7, GETDATE())")
        rows = cursor.fetchall()
        previousRunTimeTotal = rows[0][0]
        if(runTimeTotal != None) & (previousRunTimeTotal != None):
            if(previousRunTimeTotal != 0):
                if(runTimeTotal > previousRunTimeTotal):
                    runTimeDelta = runTimeTotal - previousRunTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy tuần này tăng %f giờ, tăng %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 2, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    runTimeDelta = previousRunTimeTotal - runTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy tuần này giảm %f giờ, giảm %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 2, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ chạy tuần này là %f giờ' %(runTimeTotal/60)
                data = [content , 2, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ chạy tuần này là %f giờ' %(runTimeTotal/60)
            data = [content , 2, 0, 'MC']
            InsertCommentAndWarning(data)
    #Day 
    else:
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE CONVERT(DATE, NgayCapNhat) = CONVERT(DATE, GETDATE())")
        rows = cursor.fetchall()
        runTimeTotal = rows[0][0]
        cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE CONVERT(DATE, NgayCapNhat) = CONVERT(DATE, GETDATE() - 1)")
        rows = cursor.fetchall()
        previousRunTimeTotal = rows[0][0]
        if(runTimeTotal != None) & (previousRunTimeTotal != None):
            if(previousRunTimeTotal != 0):
                if(runTimeTotal > previousRunTimeTotal):
                    runTimeDelta = runTimeTotal - previousRunTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy hôm nay tăng %f giờ, tăng %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 1, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    runTimeDelta = previousRunTimeTotal - runTimeTotal
                    efficiency = runTimeDelta/previousRunTimeTotal
                    content = 'Số giờ chạy hôm nay giảm %f giờ, giảm %f' %(runTimeDelta/60, efficiency*100) + '%'
                    data = [content , 1, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ chạy hôm nay là %f giờ' %(runTimeTotal/60)
                data = [content , 1, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ chạy hôm nay là %f giờ' %(runTimeTotal/60)
            data = [content , 1, 0, 'MC']
            InsertCommentAndWarning(data)
def breakTimeComment():
    currentDate = datetime.date.today()
    currentDay = currentDate.day
    currentDayName = currentDate.strftime("%A")
    currentMonth = currentDate.month
    currentYear = currentDate.year 

    nextDate = currentDate + timedelta(days = 1)
    #Year
    if((currentDay == 31) & (currentMonth == 12)):
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE YEAR(NgayCapNhat) = YEAR(GETDATE())")
        rows = cursor.fetchall()
        BreakDownTimeTotal = rows[0][0]
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE YEAR(NgayCapNhat) = YEAR(GETDATE()) - 1")
        rows = cursor.fetchall()
        previousBreakDownTimeTotal = rows[0][0]
        cursor.execute("SELECT COUNT(*) FROM SuaChuas WHERE YEAR(NgayYeuCau) = YEAR(GETDATE())")
        rows = cursor.fetchall()
        numbersOfTimesBreak = rows[0][0]
        if(BreakDownTimeTotal != None) & (previousBreakDownTimeTotal != None):
            if(previousBreakDownTimeTotal != 0):
                if(BreakDownTimeTotal > previousBreakDownTimeTotal):
                    BreakDownTimeDelta = BreakDownTimeTotal - previousBreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư năm này tăng %f giờ, tăng %f' %(BreakDownTimeDelta/60, efficiency*100) + '%' + '. Số lần máy hư là %d' %(numbersOfTimesBreak)
                    data = [content , 5, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    BreakDownTimeDelta = previousBreakDownTimeTotal - BreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư năm này giảm %f giờ, giảm %f' %(BreakDownTimeDelta/60, efficiency*100) + '%' + '. Số lần máy hư là %d' %(numbersOfTimesBreak)
                    data = [content , 5, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ máy hư năm này là %f giờ. Số lần máy hư là %d' %(BreakDownTimeTotal/60, numbersOfTimesBreak)
                data = [content , 5, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ máy hư năm này là %f giờ. Số lần máy hư là %d' %(BreakDownTimeTotal/60, numbersOfTimesBreak)
            data = [content , 5, 0, 'MC']
            InsertCommentAndWarning(data)
    #Quarter of the year
    if(((currentDay == 31) & (currentMonth == 3)) | ((currentDay == 30) & (currentMonth == 6)) | ((currentDay == 30) & (currentMonth == 9)) | ((currentDay == 31) & (currentMonth == 12))):
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE MONTH(NgayCapNhat) >= MONTH(GETDATE()) - 2 AND YEAR(NgayCapNhat) = YEAR(GETDATE())")
        rows = cursor.fetchall()
        BreakDownTimeTotal = rows[0][0]
        cursor.execute("SELECT COUNT(*) FROM SuaChuas WHERE MONTH(NgayYeuCau) >= MONTH(GETDATE()) - 2 AND YEAR(NgayYeuCau) = YEAR(GETDATE())")
        rows = cursor.fetchall()
        numbersOfTimesBreak = rows[0][0]
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(MONTH,-5,GETDATE()) - DAY(GETDATE()) AND NgayCapNhat <= DATEADD(MONTH,-2,GETDATE()) - DAY(GETDATE())")
        rows = cursor.fetchall()
        previousBreakDownTimeTotal = rows[0][0]
        if(BreakDownTimeTotal != None) & (previousBreakDownTimeTotal != None):
            if(previousBreakDownTimeTotal != 0):
                if(BreakDownTimeTotal > previousBreakDownTimeTotal):
                    BreakDownTimeDelta = BreakDownTimeTotal - previousBreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư quý này tăng %f giờ, tăng %f' %(BreakDownTimeDelta/60, efficiency*100) + '%' + '. Số lần máy hư là %d' %(numbersOfTimesBreak)
                    data = [content , 4, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    BreakDownTimeDelta = previousBreakDownTimeTotal - BreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư quý này giảm %f giờ, giảm %f' %(BreakDownTimeDelta/60, efficiency*100) + '%' + '. Số lần máy hư là %d' %(numbersOfTimesBreak)
                    data = [content , 4, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ máy hư quý này là %f giờ. Số lần máy hư là %d' %(BreakDownTimeTotal/60, numbersOfTimesBreak)
                data = [content , 4, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ máy hư quý này là %f giờ. Số lần máy hư là %d' %(BreakDownTimeTotal/60, numbersOfTimesBreak)
            data = [content , 4, 0, 'MC']
            InsertCommentAndWarning(data)
    #Month
    if(currentMonth != nextDate.month):
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE MONTH(NgayCapNhat) = MONTH(GETDATE()) AND YEAR(NgayCapNhat) = YEAR(GETDATE())")
        rows = cursor.fetchall()
        BreakDownTimeTotal = rows[0][0]
        cursor.execute("SELECT COUNT(*) FROM SuaChuas WHERE MONTH(NgayYeuCau) = MONTH(GETDATE()) AND YEAR(NgayYeuCau) = YEAR(GETDATE())")
        rows = cursor.fetchall()
        numbersOfTimesBreak = rows[0][0]
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(Month,-1,GETDATE()) - DAY(GETDATE())")
        rows = cursor.fetchall()
        previousBreakDownTimeTotal = rows[0][0]
        if(BreakDownTimeTotal != None) & (previousBreakDownTimeTotal != None):
            if(previousBreakDownTimeTotal != 0):
                if(BreakDownTimeTotal > previousBreakDownTimeTotal):
                    BreakDownTimeDelta = BreakDownTimeTotal - previousBreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư tháng này tăng %f giờ, tăng %f' %(BreakDownTimeDelta/60, efficiency*100) + '%'+ '. Số lần máy hư là %d' %(numbersOfTimesBreak)
                    data = [content , 3, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    BreakDownTimeDelta = previousBreakDownTimeTotal - BreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư tháng này giảm %f giờ, giảm %f' %(BreakDownTimeDelta/60, efficiency*100) + '%'+ '. Số lần máy hư là %d' %(numbersOfTimesBreak)
                    data = [content , 3, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ máy hư tháng này là %f giờ. Số lần máy hư là %d' %(BreakDownTimeTotal/60, numbersOfTimesBreak)
                data = [content , 3, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ máy hư tháng này là %f giờ. Số lần máy hư là %d' %(BreakDownTimeTotal/60, numbersOfTimesBreak)
            data = [content , 3, 0, 'MC']
            InsertCommentAndWarning(data)

    #Week
    if(currentDayName == 'Sunday'):
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(day, -7, GETDATE())")
        rows = cursor.fetchall()
        BreakDownTimeTotal = rows[0][0]
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(day, -14, GETDATE()) AND NgayCapNhat <= DATEADD(day, -7, GETDATE())")
        rows = cursor.fetchall()
        previousBreakDownTimeTotal = rows[0][0]
        if(BreakDownTimeTotal != None) & (previousBreakDownTimeTotal != None):
            if(previousBreakDownTimeTotal != 0):
                if(BreakDownTimeTotal > previousBreakDownTimeTotal):
                    BreakDownTimeDelta = BreakDownTimeTotal - previousBreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư tuần này tăng %f giờ, tăng %f' %(BreakDownTimeDelta/60, efficiency*100) + '%'
                    data = [content , 2, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    BreakDownTimeDelta = previousBreakDownTimeTotal - BreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư tuần này giảm %f giờ, giảm %f' %(BreakDownTimeDelta/60, efficiency*100) + '%'
                    data = [content , 2, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ máy hư tuần này là %f giờ' %(BreakDownTimeTotal/60)
                data = [content , 2, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ máy hư tuần này là %f giờ' %(BreakDownTimeTotal/60)
            data = [content , 2, 0, 'MC']
            InsertCommentAndWarning(data)
    #Day 
    else:
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE CONVERT(DATE, NgayCapNhat) = CONVERT(DATE, GETDATE())")
        rows = cursor.fetchall()
        BreakDownTimeTotal = rows[0][0]
        cursor.execute("SELECT SUM(BreakDownTime) FROM ThoiGianMay WHERE CONVERT(DATE, NgayCapNhat) = CONVERT(DATE, GETDATE() - 1)")
        rows = cursor.fetchall()
        previousBreakDownTimeTotal = rows[0][0]
        if(BreakDownTimeTotal != None) & (previousBreakDownTimeTotal != None):
            if(previousBreakDownTimeTotal != 0):
                if(BreakDownTimeTotal > previousBreakDownTimeTotal):
                    BreakDownTimeDelta = BreakDownTimeTotal - previousBreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư hôm nay tăng %f giờ, tăng %f' %(BreakDownTimeDelta/60, efficiency*100) + '%'
                    data = [content , 1, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    BreakDownTimeDelta = previousBreakDownTimeTotal - BreakDownTimeTotal
                    efficiency = BreakDownTimeDelta/previousBreakDownTimeTotal
                    content = 'Số giờ máy hư hôm nay giảm %f giờ, giảm %f' %(BreakDownTimeDelta/60, efficiency*100) + '%'
                    data = [content , 1, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Số giờ máy hư hôm nay là %f giờ' %(BreakDownTimeTotal/60)
                data = [content , 1, 0, 'MC']
                InsertCommentAndWarning(data)
        else:
            content = 'Số giờ máy hư hôm nay là %f giờ' %(BreakDownTimeTotal/60)
            data = [content , 1, 0, 'MC']
            InsertCommentAndWarning(data)
def EfficiencyComment():
    currentDate = datetime.date.today()
    currentMonth = currentDate.month
    currentYear = currentDate.year 
    currentDay = currentDate.day

    nextDate = currentDate + timedelta(days = 1)
    #Year
    if((currentDay == 31) & (currentMonth == 12)):
        cursor.execute('''SELECT SUM(RunningTime),
                                 SUM(WaitTime),
                                 SUM(BreakDownTime),
                                 SUM(SetUpTime),
                                 SUM(OffTime),
                                 SUM(ReadyTime) FROM ThoiGianMay WHERE YEAR(NgayCapNhat) = YEAR(GETDATE())''')
        rows = cursor.fetchall()
        runTimeTotal = rows[0][0]
        waitTimeTotal = rows[0][1]
        BreakDownTimeTotal = rows[0][2]
        setUpTimeTotal = rows[0][3]
        offTimeTotal = rows[0][4]
        readyTimeTotal = rows[0][5]
        cursor.execute('''SELECT SUM(RunningTime),
                                 SUM(WaitTime),
                                 SUM(BreakDownTime),
                                 SUM(SetUpTime),
                                 SUM(OffTime),
                                 SUM(ReadyTime) FROM ThoiGianMay WHERE YEAR(NgayCapNhat) = YEAR(GETDATE()) - 1''')
        rows = cursor.fetchall()
        previousRunTimeTotal = rows[0][0]
        previousWaitTime = rows[0][1]
        previousBreakDownTime = rows[0][2]
        previousSetUpTime = rows[0][3]
        previousOffTime = rows[0][4]
        previousReadyTime = rows[0][5]
        if(runTimeTotal != None) & (previousRunTimeTotal != None):
            timeTotal = runTimeTotal + waitTimeTotal + BreakDownTimeTotal + setUpTimeTotal + offTimeTotal + readyTimeTotal
            previousTimeTotal = previousRunTimeTotal + previousWaitTime + previousBreakDownTime + previousSetUpTime + previousOffTime + previousReadyTime
            if(timeTotal != 0) & (previousTimeTotal != 0):
                efficiency = runTimeTotal/timeTotal
                previousEfficiency = previousRunTimeTotal/previousTimeTotal
                if(efficiency > previousEfficiency):
                    efficiencyDelta = efficiency - previousEfficiency
                    content = 'Hiệu suất năm này tăng %f' %(efficiencyDelta*100) + '%'
                    data = [content , 5, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    efficiencyDelta = previousEfficiency - efficiency
                    content = 'Hiệu suất năm này giảm %f' %(efficiencyDelta*100) + '%'
                    data = [content , 5, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Hiệu suất năm này là  %f' %(efficiency*100) + '%'
                data = [content , 5, 0, 'MC']
                InsertCommentAndWarning(data)
    #Quarter of the year
    if(((currentDay == 31) & (currentMonth == 3)) | ((currentDay == 30) & (currentMonth == 6)) | ((currentDay == 30) & (currentMonth == 9)) | ((currentDay == 31) & (currentMonth == 12))):
        cursor.execute('''SELECT SUM(RunningTime),
                                 SUM(WaitTime),
                                 SUM(BreakDownTime),
                                 SUM(SetUpTime),
                                 SUM(OffTime),
                                 SUM(ReadyTime) FROM ThoiGianMay WHERE MONTH(NgayCapNhat) >= MONTH(GETDATE()) - 2 AND YEAR(NgayCapNhat) = YEAR(GETDATE())''')
        rows = cursor.fetchall()
        runTimeTotal = rows[0][0]
        waitTimeTotal = rows[0][1]
        BreakDownTimeTotal = rows[0][2]
        setUpTimeTotal = rows[0][3]
        offTimeTotal = rows[0][4]
        readyTimeTotal = rows[0][5]
        cursor.execute('''SELECT SUM(RunningTime),
                                 SUM(WaitTime),
                                 SUM(BreakDownTime),
                                 SUM(SetUpTime),
                                 SUM(OffTime),
                                 SUM(ReadyTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(MONTH,-5,GETDATE()) - DAY(GETDATE()) AND NgayCapNhat <= DATEADD(MONTH,-2,GETDATE()) - DAY(GETDATE())''')
        rows = cursor.fetchall()
        previousRunTimeTotal = rows[0][0]
        previousWaitTime = rows[0][1]
        previousBreakDownTime = rows[0][2]
        previousSetUpTime = rows[0][3]
        previousOffTime = rows[0][4]
        previousReadyTime = rows[0][5]
        if(runTimeTotal != None) & (previousRunTimeTotal != None):
            timeTotal = runTimeTotal + waitTimeTotal + BreakDownTimeTotal + setUpTimeTotal + offTimeTotal + readyTimeTotal
            previousTimeTotal = previousRunTimeTotal + previousWaitTime + previousBreakDownTime + previousSetUpTime + previousOffTime + previousReadyTime
            if(timeTotal != 0) & (previousTimeTotal != 0):
                efficiency = runTimeTotal/timeTotal
                previousEfficiency = previousRunTimeTotal/previousTimeTotal
                if(efficiency > previousEfficiency):
                    efficiencyDelta = efficiency - previousEfficiency
                    content = 'Hiệu suất quý này tăng %f' %(efficiencyDelta*100) + '%'
                    data = [content , 4, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    efficiencyDelta = previousEfficiency - efficiency
                    content = 'Hiệu suất quý này giảm %f' %(efficiencyDelta*100) + '%'
                    data = [content , 4, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Hiệu suất quý này là  %f' %(efficiency*100) + '%'
                data = [content , 4, 0, 'MC']
                InsertCommentAndWarning(data)
    #Month    
    if(currentMonth != nextDate.month):
        cursor.execute('''SELECT SUM(RunningTime),
                                 SUM(WaitTime),
                                 SUM(BreakDownTime),
                                 SUM(SetUpTime),
                                 SUM(OffTime),
                                 SUM(ReadyTime) FROM ThoiGianMay WHERE MONTH(NgayCapNhat) = MONTH(GETDATE()) AND YEAR(NgayCapNhat) = YEAR(GETDATE())''')
        rows = cursor.fetchall()
        runTimeTotal = rows[0][0]
        waitTimeTotal = rows[0][1]
        BreakDownTimeTotal = rows[0][2]
        setUpTimeTotal = rows[0][3]
        offTimeTotal = rows[0][4]
        readyTimeTotal = rows[0][5]
        cursor.execute('''SELECT SUM(RunningTime),
                                 SUM(WaitTime),
                                 SUM(BreakDownTime),
                                 SUM(SetUpTime),
                                 SUM(OffTime),
                                 SUM(ReadyTime) FROM ThoiGianMay WHERE NgayCapNhat > DATEADD(Month,-1,GETDATE()) - DAY(GETDATE())''')
        rows = cursor.fetchall()
        previousRunTimeTotal = rows[0][0]
        previousWaitTime = rows[0][1]
        previousBreakDownTime = rows[0][2]
        previousSetUpTime = rows[0][3]
        previousOffTime = rows[0][4]
        previousReadyTime = rows[0][5]
        if(runTimeTotal != None) & (previousRunTimeTotal != None):
            timeTotal = runTimeTotal + waitTimeTotal + BreakDownTimeTotal + setUpTimeTotal + offTimeTotal + readyTimeTotal
            previousTimeTotal = previousRunTimeTotal + previousWaitTime + previousBreakDownTime + previousSetUpTime + previousOffTime + previousReadyTime
            if(timeTotal != 0) & (previousTimeTotal != 0):
                efficiency = runTimeTotal/timeTotal
                previousEfficiency = previousRunTimeTotal/previousTimeTotal
                if(efficiency > previousEfficiency):
                    efficiencyDelta = efficiency - previousEfficiency
                    content = 'Hiệu suất tháng này tăng %f' %(efficiencyDelta*100) + '%'
                    data = [content , 3, 0, 'MC']
                    InsertCommentAndWarning(data)
                else:
                    efficiencyDelta = previousEfficiency - efficiency
                    content = 'Hiệu suất tháng này giảm %f' %(efficiencyDelta*100) + '%'
                    data = [content , 3, 0, 'MC']
                    InsertCommentAndWarning(data)
            else:
                content = 'Hiệu suất tháng này là  %f' %(efficiency*100) + '%'
                data = [content , 3, 0, 'MC']
                InsertCommentAndWarning(data)
def RunTimeWarning():
    cursor.execute("SELECT GiamSatmay FROM BoGiamSat")
    idMachine = cursor.fetchall()

    cursor.execute("SELECT GiaTriCap1, GiaTriCap2, GiaTriCap3 FROM Nguong WHERE Ma = 'MTB_T1'")
    rows = cursor.fetchall()
    threshold1 = rows[0][0]
    threshold2 = rows[0][1]
    threshold3 = rows[0][2]

    currentDate = datetime.date.today()
    currentMonth = currentDate.month
    currentYear = currentDate.year 
    currentDay = currentDate.day

    nextDate = currentDate + timedelta(days = 1)
    #Year
    if((currentDay == 31) & (currentMonth == 12)):
        for count in range(0, len(idMachine)):
            cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE May = ? AND YEAR(NgayCapNhat) = YEAR(GETDATE())", idMachine[0][count])
            rows = cursor.fetchall()
            runTimeTotal = rows[0][0]
            cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE May = ? AND YEAR(NgayCapNhat) = YEAR(GETDATE()) -1 ", idMachine[0][count])
            rows = cursor.fetchall()
            previousRunTimeTotal = rows[0][0]
            #Skips if the machine just put into operation this year
            if(previousRunTimeTotal != 0) & (previousRunTimeTotal != None):
                cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", idMachine[0][count])
                rows = cursor.fetchall()
                machineName = rows[0][0]
                if(runTimeTotal != None):
                    efficiency = (previousRunTimeTotal - runTimeTotal)/previousRunTimeTotal
                    if(efficiency >= threshold3):
                        content = 'Máy %s có số giờ chạy giảm %f' %(machineName, efficiency*100) + '%'+' so với năm trước'
                        data = [content ,5, 3, 'MW3']
                        InsertCommentAndWarning(data)
                    elif(efficiency >= threshold2):
                        content = 'Máy %s có số giờ chạy giảm %f' %(machineName, efficiency*100) + '%'+' so với năm trước'
                        data = [content ,5, 2, 'MW2']
                        InsertCommentAndWarning(data)
                    elif(efficiency >= threshold1):
                        content = 'Máy %s có số giờ chạy giảm %f' %(machineName, efficiency*100) + '%'+' so với năm trước'
                        data = [content ,5, 1, 'MW1']
                        InsertCommentAndWarning(data)
                else:
                    content = 'Máy %s không hoạt động trong năm này' %(machineName)
                    data = [content ,5, 3, 'MW3']
                    InsertCommentAndWarning(data)
    #Quarter of the year
    if(((currentDay == 31) & (currentMonth == 3)) | ((currentDay == 30) & (currentMonth == 6)) | ((currentDay == 30) & (currentMonth == 9)) | ((currentDay == 31) & (currentMonth == 12))):
        for count in range(0, len(idMachine)):
            cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE May = ? AND MONTH(NgayCapNhat) >= MONTH(GETDATE()) - 2 AND YEAR(NgayCapNhat) = YEAR(GETDATE())", idMachine[0][count])
            rows = cursor.fetchall()
            runTimeTotal = rows[0][0]
            cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE May = ? AND NgayCapNhat > DATEADD(MONTH,-5,GETDATE()) - DAY(GETDATE()) AND NgayCapNhat <= DATEADD(MONTH,-2,GETDATE()) - DAY(GETDATE())", idMachine[0][count])
            rows = cursor.fetchall()
            previousRunTimeTotal = rows[0][0]
            #Skips if the machine just put into operation this quater of the year
            if(previousRunTimeTotal != 0) & (previousRunTimeTotal != None):
                cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", idMachine[0][count])
                rows = cursor.fetchall()
                machineName = rows[0][0]
                if(runTimeTotal != None):
                    efficiency = (previousRunTimeTotal - runTimeTotal)/previousRunTimeTotal
                    if(efficiency >= threshold3):
                        content = 'Máy %s có số giờ chạy giảm %f' %(machineName, efficiency*100) + '%'+' so với quý trước'
                        data = [content , 4, 3, 'MW3']
                        InsertCommentAndWarning(data)
                    elif(efficiency >= threshold2):
                        content = 'Máy %s có số giờ chạy giảm %f' %(machineName, efficiency*100) + '%'+' so với quý trước'
                        data = [content , 4, 2, 'MW2']
                        InsertCommentAndWarning(data)
                    elif(efficiency >= threshold1):
                        content = 'Máy %s có số giờ chạy giảm %f' %(machineName, efficiency*100) + '%'+' so với quý trước'
                        data = [content , 4, 1, 'MW1']
                        InsertCommentAndWarning(data)
                else:
                    content = 'Máy %s không hoạt động trong quý này' %(machineName)
                    data = [content , 4, 3, 'MW3']
                    InsertCommentAndWarning(data)
    #Month    
    if(currentMonth != nextDate.month):
        for count in range(0, len(idMachine)):
            cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE May =? AND MONTH(NgayCapNhat) = MONTH(GETDATE()) AND YEAR(NgayCapNhat) = YEAR(GETDATE())", idMachine[0][count])
            rows = cursor.fetchall()
            runTimeTotal = rows[0][0]
            cursor.execute("SELECT SUM(RunningTime) FROM ThoiGianMay WHERE May = ? AND NgayCapNhat > DATEADD(Month,-1,GETDATE()) - DAY(GETDATE())", idMachine[0][count])
            rows = cursor.fetchall()
            previousRunTimeTotal = rows[0][0]
            #Skips if the machine just put into operation this month
            if(previousRunTimeTotal != 0) & (previousRunTimeTotal != None):
                cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", idMachine[0][count])
                rows = cursor.fetchall()
                machineName = rows[0][0]
                if(runTimeTotal != None):
                    efficiency = (previousRunTimeTotal - runTimeTotal)/previousRunTimeTotal
                    if(efficiency >= threshold3):
                        content = 'Máy %s có số giờ chạy giảm %f' %(machineName, efficiency*100) + '%'+' so với tháng trước'
                        data = [content , 3, 3, 'MW3']
                        InsertCommentAndWarning(data)
                    elif(efficiency >= threshold2):
                        content = 'Máy %s có số giờ chạy giảm %f' %(machineName, efficiency*100) + '%'+' so với tháng trước'
                        data = [content , 3, 2, 'MW2']
                        InsertCommentAndWarning(data)
                    elif(efficiency >= threshold1):
                        content = 'Máy %s có số giờ chạy giảm %f' %(machineName, efficiency*100) + '%'+' so với tháng trước'
                        data = [content , 3, 1, 'MW1']
                        InsertCommentAndWarning(data)
                else:
                    content = 'Máy %s không hoạt động trong tháng này' %(machineName)
                    data = [content , 3, 3, 'MW3']
                    InsertCommentAndWarning(data)
def breakNumbersWarning():
    cursor.execute("SELECT GiaTriCap1, GiaTriCap2, GiaTriCap3 FROM Nguong WHERE Ma = 'MTB_T2'")
    rows = cursor.fetchall()
    threshold1 = rows[0][0]
    threshold2 = rows[0][1]
    threshold3 = rows[0][2]

    #machineId: [Id1, Id2, ....]
    machineId = []
    #machineCode: {TypeA, TypeB, ....}
    machineCode = []
    #machineTypeBreakData: {groupName: [Team, describe, numbersTotal, numbers]}
    machineTypeBreakData = {}
    cursor.execute("SELECT May FROM SuaChuas WHERE (NgayHoanThanh IS NULL)")
    rows = cursor.fetchall()
    for tem in range(0, len(rows)):
        if rows[tem][0] != None:
            machineId.append(rows[tem][0])
    for temId in machineId:
        cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", temId)
        rows = cursor.fetchall()
        temData = rows[0][0]
        machineCode.append(temData)
    for temCode in machineCode:
        cursor.execute("SELECT Ten, ToSX, MoTa, GhiChu FROM NhomMay WHERE (GhiChu LIKE '%' + ? + '%')", temCode)
        rows = cursor.fetchall()
        for tem in range(0, len(rows)):
            if(rows[tem][0] in machineTypeBreakData):
                machineTypeBreakData[rows[tem][0]][3] += 1
            else:
                machineTypeBreakData[rows[tem][0]] = ['','', 0, 1]
                machineTypeBreakData[rows[tem][0]][0] = rows[tem][1]
                machineTypeBreakData[rows[tem][0]][1] = rows[tem][2]
                machineTypeBreakData[rows[tem][0]][2] = rows[tem][3].count(',') + 1
    for temGroupName in machineTypeBreakData:
        cursor.execute("SELECT Ten FROM ToSXes WHERE Id = ?", machineTypeBreakData[temGroupName][0])
        rows = cursor.fetchall()
        teamName = rows[0][0]
        numbersOfBreakMachine = machineTypeBreakData[temGroupName][3]
        numbersOfMachineType = machineTypeBreakData[temGroupName][2]
        efficiency = numbersOfBreakMachine/numbersOfMachineType
        if(efficiency >= threshold3):
            content = 'Số máy hư nhóm %s' %(temGroupName) + ' của tổ %s' %(teamName) + ' hiện tại quá cao, %d' %(numbersOfBreakMachine) + ' máy hư trên tổng %d máy' %(numbersOfMachineType)
            data = [content , 1, 3, 'MW3']
            InsertCommentAndWarning(data)
        elif(efficiency >= threshold2):
            content = 'Số máy hư nhóm %s' %(temGroupName) + ' của tổ %s' %(teamName) +' hiện tại quá cao, %d' %(numbersOfBreakMachine) + ' máy hư trên tổng %d máy' %(numbersOfMachineType)
            data = [content , 1, 2, 'MW2']
            InsertCommentAndWarning(data)
        elif(efficiency >= threshold1):
            content = 'Số máy hư nhóm %s' %(temGroupName) + ' của tổ %s' %(teamName) +' hiện tại quá cao, %d' %(numbersOfBreakMachine) + ' máy hư trên tổng %d máy' %(numbersOfMachineType)
            data = [content , 1, 1, 'MW1']
            InsertCommentAndWarning(data)
    return 0
def breakTimeWarning(BreakMachineId):
    cursor.execute("SELECT GiaTriCap1, GiaTriCap2, GiaTriCap3 FROM Nguong WHERE Ma = 'MTB_T5'")
    rows = cursor.fetchall()
    threshold1 = rows[0][0]
    threshold2 = rows[0][1]
    threshold3 = rows[0][2]

    for idMachine in BreakMachineId:
        if(idMachine != None):
            cursor.execute("SELECT NgayYeuCau FROM SuaChuas WHERE May = ? AND NgayHoanThanh IS NULL", idMachine)
            rows = cursor.fetchall()
            breakTimeStart = rows[0][0]
            currentDateTime = datetime.datetime.today()
            breakTimeTotal = currentDateTime - breakTimeStart
            breakTimeTotal= (int)(breakTimeTotal.total_seconds()/60)
            if(breakTimeTotal >= threshold3*60):
                cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", idMachine)
                rows = cursor.fetchall()
                machineName = rows[0][0]
                content = 'Thời gian máy %s hư là %s giờ, vượt qua %s giờ' %(machineName, breakTimeTotal/60, threshold3)
                data = [content , 1, 3, 'MW3']
                InsertCommentAndWarning(data)
            elif(breakTimeTotal >= threshold2*60):
                cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", idMachine)
                rows = cursor.fetchall()
                machineName = rows[0][0]
                content = 'Thời gian máy %s hư là %s giờ, vượt qua %s giờ' %(machineName, breakTimeTotal/60, threshold2)
                data = [content , 1, 2, 'MW2']
                InsertCommentAndWarning(data)
            elif(breakTimeTotal >= threshold1*60):
                cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", idMachine)
                rows = cursor.fetchall()
                machineName = rows[0][0]
                content = 'Thời gian máy %s hư là %s giờ, vượt qua %s giờ' %(machineName, breakTimeTotal/60, threshold1)
                data = [content , 1, 1, 'MW1']
                InsertCommentAndWarning(data)
def EfficiencyWarning():
    cursor.execute("SELECT GiamSatmay FROM BoGiamSat")
    idMachine = cursor.fetchall()

    cursor.execute("SELECT GiaTriCap1, GiaTriCap2, GiaTriCap3 FROM Nguong WHERE Ma = 'MTB_T3'")
    rows = cursor.fetchall()
    threshold1 = rows[0][0]
    threshold2 = rows[0][1]
    threshold3 = rows[0][2]

    currentDate = datetime.date.today()
    currentMonth = currentDate.month
    currentYear = currentDate.year 
    currentDay = currentDate.day

    nextDate = currentDate + timedelta(days = 1)
    #Month
    if(currentMonth != nextDate.month):
        for count in range(0, len(idMachine)):
            cursor.execute('''SELECT SUM(RunningTime),
                                     SUM(WaitTime),
                                     SUM(BreakDownTime),
                                     SUM(SetUpTime),
                                     SUM(OffTime),
                                     SUM(ReadyTime) FROM ThoiGianMay WHERE May = ? AND MONTH(NgayCapNhat) = MONTH(GETDATE()) AND YEAR(NgayCapNhat) = YEAR(GETDATE())''', idMachine[0][count])
            rows = cursor.fetchall()
            runTimeTotal = rows[0][0]
            waitTimeTotal = rows[0][1]
            BreakDownTimeTotal = rows[0][2]
            setUpTimeTotal = rows[0][3]
            offTimeTotal = rows[0][4]
            readyTimeTotal = rows[0][5]
            cursor.execute('''SELECT SUM(RunningTime),
                                     SUM(WaitTime),
                                     SUM(BreakDownTime),
                                     SUM(SetUpTime),
                                     SUM(OffTime),
                                     SUM(ReadyTime) FROM ThoiGianMay WHERE May = ? AND NgayCapNhat > DATEADD(Month,-1,GETDATE()) - DAY(GETDATE())''', idMachine[0][count])
            rows = cursor.fetchall()
            previousRunTimeTotal = rows[0][0]
            previousWaitTime = rows[0][1]
            previousBreakDownTime = rows[0][2]
            previousSetUpTime = rows[0][3]
            previousOffTime = rows[0][4]
            previousReadyTime = rows[0][5]
            #Skips if either runTimeTotal or previousRunTimeTotal is null, because runTimeWarning warned
            if(runTimeTotal != None) & (previousRunTimeTotal != None):
                timeTotal = runTimeTotal + waitTimeTotal + BreakDownTimeTotal + setUpTimeTotal + offTimeTotal + readyTimeTotal
                previousTimeTotal = previousRunTimeTotal + previousWaitTime + previousBreakDownTime + previousSetUpTime + previousOffTime + previousReadyTime
                #Skips if either runTimeTotal or previousRunTimeTotal equel 0, because runTimeWarning warned
                if(timeTotal != 0) & (previousTimeTotal != 0):
                    efficiency = runTimeTotal/timeTotal
                    previousEfficiency = previousRunTimeTotal/previousTimeTotal
                    #Skips if not much has changed
                    if(efficiency < threshold3*previousEfficiency):
                        efficiencyDelta = previousEfficiency - efficiency
                        cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", idMachine[0][count])
                        rows = cursor.fetchall()
                        machineName = rows[0][0]
                        content = 'Hiệu suất máy %s giảm %f' %(machineName, efficiencyDelta*100) + '%' +' so với tháng trước'
                        data = [content , 3, 3, 'MW3']
                        InsertCommentAndWarning(data)
                    elif(efficiency < threshold2*previousEfficiency):
                        efficiencyDelta = previousEfficiency - efficiency
                        cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", idMachine[0][count])
                        rows = cursor.fetchall()
                        machineName = rows[0][0]
                        content = 'Hiệu suất máy %s giảm %f' %(machineName, efficiencyDelta*100) + '%' +' so với tháng trước'
                        data = [content , 3, 2, 'MW2']
                        InsertCommentAndWarning(data)
                    elif(efficiency < threshold1*previousEfficiency):
                        efficiencyDelta = previousEfficiency - efficiency
                        cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", idMachine[0][count])
                        rows = cursor.fetchall()
                        machineName = rows[0][0]
                        content = 'Hiệu suất máy %s giảm %f' %(machineName, efficiencyDelta*100) + '%' +' so với tháng trước'
                        data = [content , 3, 1, 'MW1']
                        InsertCommentAndWarning(data)
def Recommendations():
    currentDate = datetime.date.today()
    currentMonth = currentDate.month
    currentYear = currentDate.year 
    currentDay = currentDate.day

    machiningTeamData = {}
#    if ((currentDay == 30) & (currentMonth == 6)) | ((currentDay == 31) & (currentMonth == 12)):
    cursor.execute("SELECT Ten, ToSX, GhiChu FROM NhomMay")
    rows = cursor.fetchall()
    for tem in range(0,len(rows)):
        machiningTeamData[rows[tem][0]] = ['',0, '']
        machiningTeamData[rows[tem][0]][0] = rows[tem][0]
        machiningTeamData[rows[tem][0]][1] = rows[tem][1]
        machiningTeamData[rows[tem][0]][2] = rows[tem][2]
    for groupName in machiningTeamData:
        machineIdOfGroup = []
        nameData = machiningTeamData[groupName][2].split(', ')
        for tem in range(0, len(nameData)):
            cursor.execute("SELECT Id FROM Mays WHERE MaSo = ?", nameData[tem])
            rows = cursor.fetchall()
            if rows != []:
                machineIdOfGroup.append(rows[0][0])
        if(len(machineIdOfGroup) > 0):
            cursor.execute("SELECT GiaTriCap1, GiaTriCap2 FROM NhomMay WHERE Ten = ?", groupName)
            rows = cursor.fetchall()
            threshold1 = rows[0][0]
            threshold2 = rows[0][1]
            avgOpeningTimeData = CalAvgOpeningTime(machineIdOfGroup)
            avgOpeningTime =avgOpeningTimeData[-1]
            if(avgOpeningTime >= threshold2):
                MaintainOrReplace(machineIdOfGroup)
                Allocate(machiningTeamData[groupName], avgOpeningTimeData)
                AddnumbersOfMachine()
            elif(avgOpeningTime >= threshold1*60):
                MaintainOrReplace(machineIdOfGroup)
            else:
                if len(machineIdOfGroup) > 1:
                    cuttingMachineNumbers = (int)((len(machineIdOfGroup) - avgOpeningTime/(threshold1*60))//1)
                    content = 'cần giảm biên chế %d máy thuộc nhóm máy %s của tổ %s' %(cuttingMachineNumbers, groupName, machiningTeamData[groupName][1])
    return 0
def CalAvgOpeningTime(machineIdOfGroup):
    currentDate = datetime.date.today()
    #avgOpeningTime: [x1, x2,....,x]
    avgOpeningTime = []
    numbersOfDay = 0
    for id in machineIdOfGroup:
        temOpeningTime = 0
        cursor.execute("SELECT RunningTime, SetUpTime, ReadyTime FROM ThoiGianMay WHERE May = ? AND NgayCapNhat > DATEADD(Month,-6,GETDATE()) - DAY(GETDATE())", id)
        rows = cursor.fetchall()
        for tem in range(0,len(rows)):
            temOpeningTime += rows[tem][0] + rows[tem][1] + rows[tem][2]
        avgOpeningTime.append(temOpeningTime)
    for tem in range(0, 6):
        temMonth = currentDate + dateutil.relativedelta.relativedelta(months = -tem)
        numbersOfDay += calendar.monthrange(temMonth.year, temMonth.month)[1]
    for tem in range(0, len(avgOpeningTime)):
        avgOpeningTime[tem] /= (numbersOfDay)
    avgOpeningTime.append(sum(avgOpeningTime)/len(avgOpeningTime))
    return avgOpeningTime
def MaintainOrReplace(machineIdOfGroup):
    for id in machineIdOfGroup:
        breakAllocation = []
        dispersionQuotient = 0
        for tem in range(11, -1, -1):
            cursor.execute("SELECT COUNT(*) FROM SuaChuas WHERE May = ? AND NgayYeuCau > DATEADD(Month,-?,GETDATE()) - DAY(GETDATE()) AND NgayYeuCau <= DATEADD(Month,-? +1,GETDATE()) - DAY(GETDATE())", id, tem, tem)
            rows = cursor.fetchall()
            breakAllocation.append(rows[0][0])
        threshold = sum(breakAllocation)/len(breakAllocation) - min(breakAllocation)
        levelPlus = 1
        trendIndicator = 0
        previousSate = 0
        classify = []
        for tem in range(1,12):
            trendIndicator += breakAllocation[tem] - breakAllocation[tem -1]
            if((breakAllocation[tem] > threshold) & (breakAllocation[tem - 1] > threshold)):
                dispersionQuotient += levelPlus
                levelPlus += 1
                if(previousSate == 0):
                    classify.append(tem)
                previousSate = 1
            else:
                if(levelPlus > 1):
                    if((breakAllocation[tem - 1] > threshold*2) & (breakAllocation[tem + 1] > threshold*2)):
                        previousSate = 1
                    else:
                        classify.append(tem)
                        previousSate = 0
                else:
                    if(previousSate != 0):
                        classify.append(tem)
                levelPlus = 1
        if(sum(breakAllocation) > 5):
            if(dispersionQuotient < 5):
                if(trendIndicator > 0):
                    cursor.execute("SELECT NamSX FROM Mays WHERE Id = ?", id)
                    rows = cursor.fetchall()
                    timeDelta = datetime.datetime.now().year - rows[0][0]
                    cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", Id)
                    rows = cursor.fetchall()
                    machineName = rows[0][0]
                    if(timeDelta >= 10):
                        content = 'Nên xem xét thay thế máy %s' %(machineName)
                    else:
                        content = 'nên xem xét nâng cấp máy %s' %(machineName) 
            else:
                pass
def Allocate(machiningTeamData, avgOpeningTimeData):
    variance = 0
    for tem in range(0, len(avgOpeningTimeData) - 1):
        variance += (avgOpeningTimeData[tem] - avgOpeningTimeData[-1])**2
    standardDeviation = (variance/(len(avgOpeningTimeData) - 1))**(0.5)
    if(standardDeviation > 0.2*avgOpeningTimeData[-1]):
        content = 'Cần phân bổ đều công việc nhóm máy %s' %(machiningTeamData[0]) + ' của tổ %s' %(machiningTeamData[1])
    else:
        return 0
def AddnumbersOfMachine():
    pass
def InsertCommentAndWarning(data):
    if(CheckExistData(data[0])):
        #content of comment or warning
        content = data[0]
        #1 : day, 2: week, 3: month, 4: quater of the year, 5: year
        dateClass = data[1]
        warningLevel = data[2]
        warningCode = data[3]
        cursor.execute('''INSERT INTO NhanXet_CanhBao (NgayCapNhat, NoiDung, PhanLoai, MaCanhBao, CapDo) 
                                VALUES (GETDATE(), ?, ?, ?, ?)''', content, dateClass, warningCode, warningLevel)
        conection.commit()
    return 0
def CheckExistData(content):
    cursor.execute("SELECT COUNT(*) FROM NhanXet_CanhBao WHERE NoiDung = ? AND (CONVERT(DATE, NgayCapNhat) = CONVERT(DATE, GETDATE()))", content)
    rows = cursor.fetchall()
    count = rows[0][0]
    if(count == 0):
        return 1
    else: 
        return 0
#############################################################################################################################################################


#Main function
#previousData: {GiamSatMay: (GiamSatMay, ThoiGianCapNhat, CongSuatPhaA, CongSuatPhaB, CongSuatPhaC, trangThai)}
previousData = {}
#resultData: {GiamSatmay: (RunningTime, WaitTime, BreakDownTime, SetUpTime, OffTime, congsuatRunning=NangLuongRunning/RunningTime, TongNangLuong/60, ReadyTime)}
resultData = {}
#Connect to database
conection = SQLconection()
cursor = conection.cursor()
#Get min and max of id for access
idMin = MinIdOfCurentDate()
idMax = MaxIdOfCurentDate()
while (True):
    #BreakMachineId: [Id1, Id2, ....]
    BreakMachineId = LoadBreakMachineId()
    if Should_run():
        print("running task of day")
        #Load data of machines that are break or have been repaired current date
        #breakData: [(May, NgayYeuCau, NgayHoanThanh)]
        breakData = LoadbreakData()
        #Calulate time break of each break machine
        #breakTime: {May: breakTime}
        breakTime = Handlerbreak(breakData)
        #Insert or update break time to result data before insert and update data of day month
        for idMachine in breakTime:
            if(idMachine in resultData):
                resultData[idMachine][2] = breakTime[idMachine]
            else:
                if idMachine != None:
                    resultData[idMachine] = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
                    resultData[idMachine][2] = breakTime[idMachine]
        #insert and update data of day and month
        InsertDataOfDay(resultData)
        UpdateDataOfMonth(resultData)
        #Comment
        RunTimeComment()
        breakTimeComment()
        EfficiencyComment()
        #Warning
        RunTimeWarning()
        EfficiencyWarning()
        breakTimeWarning(BreakMachineId)
        #Recommend()
        print("Running task done\n")
        previousData = {}
        resultData = {}
        UpdateStateMachine()

        wait_seconds = 360
        print("Sleeping for 6 minutes...")
        time.sleep(wait_seconds)
    else:
        idMax = MaxIdOfCurentDate()
        if(idMin == None):
            idMin = MinIdOfCurentDate()
        #Skip if don't have data of current date
        if((idMax != None) & (idMin != None)):
            if(idMin <= idMax):
                for id in range(idMin,idMax+1):
                    #data: (GiamSatMay, ThoiGianCapNhat, CongSuatPhaA, CongSuatPhaB, CongSuatPhaC, trangThai)
                    data = GetData(id)
                    #Skip if don't have data at this            
                    if (data != []):
                        if(data[0] != None):
                            # if GiamSatMay in previousData
                            if(data[0] in previousData):
                                # if GiamSatmay isn't break
                                if (data[0] not in BreakMachineId):
                                    #temResultData: (RunningTime, WaitTime, BreakDownTime, SetUpTime, OffTime, congsuatRunning=NangLuongRunning, TongNangLuong, ReadyTime)
                                    temResultData = CalculateData(previousData[data[0]], data)
                                    if(temResultData != 0):
                                        #count: 0, 1, 2 .....
                                        for count in range(0, len(temResultData)):
                                            #accumulate time and energey
                                            resultData[data[0]][count] += temResultData[count]
                                        #Delete data when state of current data same state of previous data and powerDelta <= 0.1*currentData
                                        #OptimizeData(id, previousData[data[0]], data)
                                    previousData[data[0]] = data
                                elif(previousData[data[0]][5] != 6):
                                    previousData[data[0]] = data
                                else:
                                    cursor.execute("DELETE LichSuMay WHERE Id = ?", id)                          
                            else:
                                resultData[data[0]] = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
                                previousData[data[0]] = data
            checkOff(previousData)
            InsertDataOfDay(resultData)
            idMin = idMax + 1
        breakNumbersWarning()
        Recommendations()
        #If in sleep time or done task, sleep
        #wait_seconds = Get_wait_seconds()
        wait_seconds = 300
        print("Sleeping for 5 minutes...")
        time.sleep(wait_seconds)
