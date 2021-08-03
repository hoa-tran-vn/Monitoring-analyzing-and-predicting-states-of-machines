import pyodbc 
import time, datetime
from datetime import timedelta, date
import dateutil.relativedelta
import calendar
import pandas as pd
from sklearn.tree import DecisionTreeClassifier 
from sklearn.model_selection import train_test_split 
from sklearn import metrics 
from sklearn import tree
from sklearn.tree import export_graphviz
from sklearn.externals.six import StringIO  
from IPython.display import Image  
import pydotplus
import graphviz
from graphviz import Graph
from sklearn import preprocessing
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_squared_error
import random
from sklearn.metrics import accuracy_score

def CollectingDataForTrue(previousErrDate):
    breakDown = 1
    cursor.execute("SELECT May, NgayYeuCau FROM SuaChuas WHERE NgayYeuCau > DATEADD(Month,-1,GETDATE()) - DAY(GETDATE())")
    rows = cursor.fetchall()
    errDataList = []
    for element in range(0, len(rows)):
        temData = rows[element]
        if((temData[0] != None) & (temData[1] != None)):
            errDataList.append(temData)
    for element in range(0, len(errDataList)):
        machineId = errDataList[element][0]
        currentDate = errDataList[element][1]
        currentDate = currentDate.date()
        cursor.execute("SELECT COUNT (*) FROM BoGiamSat WHERE GiamSatmay = ?", machineId)
        rows = cursor.fetchall()
        check = rows[0][0]
        if(check > 0):
            if(machineId in previousErrDate):
                cursor.execute("SELECT SUM(RunningTime), SUM(SetUpTime), SUM(ReadyTime), SUM(OffTime), SUM(waitTime) FROM ThoiGianMay WHERE may = ? AND NgayCapNhat > ? AND NgayCapNhat <= ?", machineId, previousErrDate[machineId], currentDate)
                rows = cursor.fetchall()
                if(rows[0][0] != None):
                    openMachineTime = rows[0][0] + rows[0][1] + rows[0][2]
                    OffTime = rows[0][3] + rows[0][4]
                    #InsertTrainingData(openMachineTime, OffTime, machineId, currentDate, breakDown)
            else:
                cursor.execute("SELECT SUM(RunningTime), SUM(SetUpTime), SUM(ReadyTime), SUM(OffTime), SUM(waitTime) FROM ThoiGianMay WHERE may = ? AND NgayCapNhat > DATEADD(Month,-1,?) AND NgayCapNhat <= ?", machineId, currentDate, currentDate)
                rows = cursor.fetchall()
                if(rows[0][0] != None):
                    openMachineTime = rows[0][0] + rows[0][1] + rows[0][2]
                    OffTime = rows[0][3] + rows[0][4]
                    #InsertTrainingData(openMachineTime, OffTime, machineId, currentDate, breakDown)
        previousErrDate[machineId] = currentDate
    return previousErrDate

def CollectingDataForFalse(previousErrDate):
    breakDown = 0    
    cursor.execute("SELECT GiamSatmay FROM BoGiamSat")
    rows = cursor.fetchall()
    machineIdTotal = []
    for element in range(0, len(rows)):
        temData = rows[element][0]
        if((temData != None)):
            machineIdTotal.append(temData)
    cursor.execute("SELECT COUNT(*) FROM DataTraining WHERE BreakDown = 1")
    rows = cursor.fetchall()
    numbersOfBrDown_True = rows[0][0]
    cursor.execute("SELECT COUNT(*) FROM DataTraining WHERE BreakDown = 0")
    rows = cursor.fetchall()
    numbersOfBrDown_False = rows[0][0]
    numbersOfBrDown_False = (int)(numbersOfBrDown_True - numbersOfBrDown_False)
    if(numbersOfBrDown_False > 0):
        machineIdTotal = random.sample(machineIdTotal, numbersOfBrDown_False)
        for element in range(0, len(machineIdTotal)):
            machineId = machineIdTotal[element]
            if(machineId in previousErrDate):
                cursor.execute("SELECT SUM(RunningTime), SUM(SetUpTime), SUM(ReadyTime), SUM(OffTime), SUM(waitTime) FROM ThoiGianMay WHERE may = ? AND NgayCapNhat > ?", machineId, previousErrDate[machineId])
                rows = cursor.fetchall()
                if(rows[0][0] != None):
                    openMachineTime = rows[0][0] + rows[0][1] + rows[0][2]
                    OffTime = rows[0][3] + rows[0][4]
                    #InsertTrainingData(openMachineTime, OffTime, machineId, currentDate, breakDown)
            else:
                cursor.execute("SELECT SUM(RunningTime), SUM(SetUpTime), SUM(ReadyTime), SUM(OffTime), SUM(waitTime) FROM ThoiGianMay WHERE may = ?", machineId)
                rows = cursor.fetchall()
                if(rows[0][0] != None):
                    openMachineTime = rows[0][0] + rows[0][1] + rows[0][2]
                    OffTime = rows[0][3] + rows[0][4]
                    #InsertTrainingData(openMachineTime, OffTime, machineId, currentDate, breakDown)

def InsertTrainingData(openMachineTime, OffTime, machineId, currentDate, breakDown):
    cursor.execute("SELECT MaSo, NhanHieu, NamSX FROM Mays WHERE Id = ?", machineId)
    rows = cursor.fetchall()
    machineType = rows[0][0]
    machineType = machineType.rstrip('0123456789')
    brand = rows[0][1]
    usedTime =rows[0][2]
    if(usedTime != None):
        usedTime =  currentDate.year - int(usedTime)
    cursor.execute("SELECT COUNT(*) FROM SuaChuas WHERE May = ? and NgayYeuCau < ?", machineId, currentDate)
    rows = cursor.fetchall()
    numbersOfBrDown = rows[0][0]
    #cursor.execute("INSERT INTO DataTraining (OpenTime, OffTime, UsedTime, MachineType, Brand, NumbersOfBrDown, BreakDown) VALUES (?, ?, ?, ?, ?, ?, ?)", openMachineTime, OffTime, usedTime, machineType, brand, numbersOfBrDown, breakDown)
    #cursor.commit()

def TrainingDecisionTree():
    cursor.execute("SELECT OpenTime, OffTime, UsedTime, MachineType, Brand, NumbersOfBrDown, BreakDown FROM DataTraining")
    rows = cursor.fetchall()
    data = []
    data_train_temp = []
    for element in range(0, len(rows)):
        temData = list(rows[element])
        data.append(temData)
        if(temData[2] != None):
            data_train_temp.append(temData)
    col_names = ['OpenTime', 'OffTime', 'UsedTime', 'MachineType', 'Brand', 'NumbersOfBrDown', 'label']
    pima = pd.DataFrame(data, columns=col_names)


    data_for_UsedTime = pima[['NumbersOfBrDown', 'label', 'UsedTime']]
    data_with_null = data_for_UsedTime.loc[data_for_UsedTime['UsedTime'].isnull()]
    data_without_null = data_for_UsedTime.dropna()
    train_data_without_null_X = data_without_null[['NumbersOfBrDown', 'label']]
    train_data_without_null_Y = data_without_null.UsedTime
    reg = LinearRegression().fit(train_data_without_null_X, train_data_without_null_Y)

    test_data_with_null_X = data_with_null[['NumbersOfBrDown', 'label']]
    data_with_null['UsedTime'] = reg.predict(test_data_with_null_X)
    pima.UsedTime.fillna(data_with_null['UsedTime'], inplace = True)

    pima["Brand"].fillna("Unkown", inplace = True) 

    typeLe = preprocessing.LabelEncoder()
    typeLe.fit(['E','EN','ET','LT','MD','ML','MP','MT','NM','P','PC','TC','W'])
    brandLe = preprocessing.LabelEncoder()
    brandLe.fit(['6P12','AKIRA SEIKI','Aristech','AZUMA','CHMER','CHUN SHIN','DMG','DYNA','ENSHU','EUMACH','FEELER','Funseng','GIẢI PHÓNG','GOLDSAN','HAAS','HARTFORT','HAWACHEON','Heckert-DDR','HERO','HOWA-SANG YO','HYFAIR','IKEDA','IKEGAI','JAINNHER','JINGDIAO','Jotes SPG','KANTO','KASUGA','KAY','KEIYOSEIKI','KINGSPARK','KURAKI','MAZAK','MBO-35KW','MITSUISEKI','MORISEKI','NAKABO','OKK','OKUMA','PALMARY','Ponar - Tarnow','PROTH','SEIBU','SEIKI','SISMA','SODICK','SUMITUMO','TAKISAWA','TOAKIKAI','TONGIL','TOS-HOSTIVAR','TOYODA','TRENS TRENCIN','URAWA','USSR','UY-DONGGUAN','WAIDA','WENZEL','WOODPECKER','YAMAZAKI','YEB', 'Unkown'])
    feature_cols = ['OpenTime', 'OffTime', 'UsedTime', 'MachineType', 'Brand', 'NumbersOfBrDown']
    pima['MachineType'] = typeLe.transform(pima.MachineType)
    pima['Brand'] = brandLe.transform(pima.Brand)
    X = pima[feature_cols] # Features
    y = pima.label # Target variable
    # Split dataset into training set and test set
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=7) # 80% training and 20% test
    X_train_temp, X_validate, Y_train_temp, y_validate = train_test_split(X_train, y_train, test_size=0.2, random_state=7)


    # Create Decision Tree classifer object
    clf = DecisionTreeClassifier(criterion="entropy", max_depth=8)
    path = clf.cost_complexity_pruning_path(X_train_temp, Y_train_temp)
    ccp_alphas, impurities = path.ccp_alphas, path.impurities

    clfs = []
    for ccp_alpha in ccp_alphas:
        clf = DecisionTreeClassifier(criterion="entropy", max_depth=8, ccp_alpha=ccp_alpha)
        clf.fit(X_train_temp, Y_train_temp)
        clfs.append(clf)

    train_scores = [clf.score(X_train_temp, Y_train_temp) for clf in clfs]
    test_scores = [clf.score(X_validate, y_validate) for clf in clfs]
    alpha = ccp_alphas[test_scores.index(max(test_scores))]

    clf = DecisionTreeClassifier(criterion="entropy", max_depth=8, ccp_alpha=alpha)
    clf.fit(X_train,y_train)
    pred=clf.predict(X_test)
    print(accuracy_score(y_test, pred))
    dot_data = StringIO()
    export_graphviz(clf, out_file=dot_data,  
                    filled=True, rounded=True,
                    special_characters=True, feature_names = feature_cols)
    graph = pydotplus.graph_from_dot_data(dot_data.getvalue())  
    graph.write_png('optimalDiabetes.png')
    Image(graph.create_png())
    return clf

def GetDataToBePredicted():
    dataToBePredicted = {}
    cursor.execute("SELECT GiamSatmay FROM BoGiamSat")
    rows = cursor.fetchall()
    machineIdTotal = []
    for element in range(0, len(rows)):
        temData = rows[element][0]
        if((temData != None)):
            machineIdTotal.append(temData)
    for element in range(0, len(machineIdTotal)):
        dataToBePredicted_Temp = []
        cursor.execute("SELECT MAX(NgayYeuCau) FROM SuaChuas WHERE May = ?", machineIdTotal[element])
        rows = cursor.fetchall()
        errDate = rows[0][0]
        if(errDate != None):
            cursor.execute("SELECT SUM(RunningTime), SUM(SetUpTime), SUM(ReadyTime), SUM(OffTime), SUM(waitTime) FROM ThoiGianMay WHERE may = ? AND NgayCapNhat > ?", machineIdTotal[element], errDate)
            rows = cursor.fetchall()
            if(rows[0][0] != None):
                openMachineTime = rows[0][0] + rows[0][1] + rows[0][2]
                OffTime = rows[0][3] + rows[0][4]
        else:
            cursor.execute("SELECT SUM(RunningTime), SUM(SetUpTime), SUM(ReadyTime), SUM(OffTime), SUM(waitTime) FROM ThoiGianMay WHERE may = ?", machineIdTotal[element])
            rows = cursor.fetchall()
            if(rows[0][0] != None):
                openMachineTime = rows[0][0] + rows[0][1] + rows[0][2]
                OffTime = rows[0][3] + rows[0][4]
        cursor.execute("SELECT SUM(RunningTime), SUM(SetUpTime), SUM(ReadyTime), SUM(OffTime), SUM(waitTime) FROM ThoiGianMay WHERE may = ? AND NgayCapNhat > DATEADD(Day,-7,GETDATE())", machineIdTotal[element])
        rows = cursor.fetchall()
        if(rows[0][0] != None):
            openMachineTime = openMachineTime + rows[0][0] + rows[0][1] + rows[0][2]
            OffTime = OffTime + rows[0][3] + rows[0][4]
        try:
            dataToBePredicted_Temp.append(openMachineTime)
            dataToBePredicted_Temp.append(OffTime)
            cursor.execute("SELECT MaSo, NhanHieu, NamSX FROM Mays WHERE Id = ?", machineIdTotal[element])
            rows = cursor.fetchall()
            machineType = rows[0][0]
            machineType = machineType.rstrip('0123456789')
            brand = rows[0][1]
            usedTime =rows[0][2]
            if(usedTime != None):
                usedTime =  currentDate.year - int(usedTime)
            cursor.execute("SELECT COUNT(*) FROM SuaChuas WHERE May = ?", machineIdTotal[element])
            rows = cursor.fetchall()
            numbersOfBrDown = rows[0][0]
            dataToBePredicted_Temp.append(usedTime)
            dataToBePredicted_Temp.append(machineType)
            dataToBePredicted_Temp.append(brand)
            dataToBePredicted_Temp.append(numbersOfBrDown)
            dataToBePredicted[machineIdTotal[element]] = dataToBePredicted_Temp
        except:
            print('passing')
    return dataToBePredicted

def Predict(dataToBePredicted, clf):
    col_names = ['OpenTime', 'OffTime', 'UsedTime', 'MachineType', 'Brand', 'NumbersOfBrDown']
    for machineId in dataToBePredicted:
        if((dataToBePredicted[machineId][0] != None) and (dataToBePredicted[machineId][1] != None) and (dataToBePredicted[machineId][2] != None) and (dataToBePredicted[machineId][3] != None) and (dataToBePredicted[machineId][4] != None) and (dataToBePredicted[machineId][5] != None)):
            data_Temp = dataToBePredicted[machineId]
            data_Temp2 = [0]
            data_Temp2[0] = data_Temp
            x_Temp = pd.DataFrame(data_Temp2, columns=col_names)
            typeLe = preprocessing.LabelEncoder()
            typeLe.fit(['E','EN','ET','LT','MD','ML','MP','MT','NM','P','PC','TC','W'])
            brandLe = preprocessing.LabelEncoder()
            brandLe.fit(['6P12','AKIRA SEIKI','Aristech','AZUMA','CHMER','CHUN SHIN','DMG','DYNA','ENSHU','EUMACH','FEELER','Funseng','GIẢI PHÓNG','GOLDSAN','HAAS','HARTFORT','HAWACHEON','Heckert-DDR','HERO','HOWA-SANG YO','HYFAIR','IKEDA','IKEGAI','JAINNHER','JINGDIAO','Jotes SPG','KANTO','KASUGA','KAY','KEIYOSEIKI','KINGSPARK','KURAKI','MAZAK','MBO-35KW','MITSUISEKI','MORISEKI','NAKABO','OKK','OKUMA','PALMARY','Ponar - Tarnow','PROTH','SEIBU','SEIKI','SISMA','SODICK','SUMITUMO','TAKISAWA','TOAKIKAI','TONGIL','TOS-HOSTIVAR','TOYODA','TRENS TRENCIN','URAWA','USSR','UY-DONGGUAN','WAIDA','WENZEL','WOODPECKER','YAMAZAKI','YEB', 'Unkown'])
            x_Temp['MachineType'] = typeLe.transform(x_Temp.MachineType)
            x_Temp['Brand'] = brandLe.transform(x_Temp.Brand)
            y_pred = clf.predict(x_Temp)
            if(y_pred == 1):
                print(data_Temp)
                cursor.execute("SELECT MaSo FROM Mays WHERE Id = ?", machineId)
                rows = cursor.fetchall()
                machineCode = rows[0][0]
                content = 'Máy %s' %(machineCode) + ' có khả năng sắp hư. Khuyến nghị bảo trì!'
                #cursor.execute('''INSERT INTO NhanXet_CanhBao (NgayCapNhat, NoiDung, PhanLoai, MaCanhBao, CapDo) 
                #                VALUES (GETDATE(), ?, 2, 'MR', 0)''', content)
                #cursor.commit()
###########################################################################################################################################################################
#Main function
conection = pyodbc.connect("Driver={SQL Server Native Client 11.0};"               
                               "Server=DESKTOP-AQ3PPMN\SQLEXPRESS;"
                               "Database=OneDuyKhanh4;"
                               "username=Hoa;"
                               "password=123456;"
                               "Trusted_Connection=yes;")
cursor = conection.cursor()
previousErrDate = {}
clf = TrainingDecisionTree()
# cursor.execute("SELECT May, NgayYeuCau FROM SuaChuas")
# rows = cursor.fetchall()
# for element in range(0, len(rows)):
#     previousErrDate[rows[element][0]] = rows[element][1]
while(1):
    currentDate = datetime.date.today()
    currentDayName = currentDate.strftime("%A")
    currentMonth = currentDate.month
    nextDate = currentDate + timedelta(days = 1)
    #Collecting data for training, done once a month
    if(currentMonth != nextDate.month):
        #For "True" label
        previousErrDate = CollectingDataForTrue(previousErrDate)
        #For "False" lable
        CollectingDataForFalse(previousErrDate)
        #Rebuilding module of Decision Tree
        clf = TrainingDecisionTree()
    if(currentDayName == 'Sunday'):
        dataToBePredicted = GetDataToBePredicted()
        Predict(dataToBePredicted, clf)
    print("sleeping for 24 hours....")
    time.sleep(86400)