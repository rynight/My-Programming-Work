"""
Created on Thu Nov 17 00:55:09 2022

@author: ryanmark
"""

import pandas as pd # for data manipulation
import numpy as np
from numpy import asarray
from numpy import savetxt

from sklearn.model_selection import train_test_split # for splitting the data into train and test samples
from sklearn.metrics import classification_report # for model evaluation metrics
from sklearn.ensemble import GradientBoostingClassifier

SeedData = np.genfromtxt("MNCAATourneySeeds.csv", delimiter=",", skip_header=1, dtype=str)
RegSeasonData = np.genfromtxt("MRegularSeasonDetailedResults.csv", delimiter=",", skip_header=1, dtype=str)
TourneyData = np.genfromtxt("MNCAATourneyCompactResults.csv", delimiter=",", skip_header=1, dtype=float)
RatingData = np.genfromtxt("RatingsDataRY.csv", delimiter=",", skip_header=1, dtype=float)
KenData = np.genfromtxt("NCAA2021_Kenpom.csv", delimiter=",", skip_header=1, dtype=float)
TeamsData = np.genfromtxt("MTeams.csv", delimiter=",", skip_header=1, dtype=str)

def swap(List, index1, index2):
    temp = List[index1].copy()
    List[index1] = List[index2].copy()
    List[index2] = temp.copy()
    
def getKenRating1(teamNum, Year):
    for team in KenData:
        #print(team)
        if int(team[0]) < Year:
            continue
        elif int(team[0]) > Year:
            continue
        elif int(team[1]) != teamNum:
            continue
        
        return float(team[6])
    
    return 0.0

def getKenRating2(teamNum, Year):
    for team in KenData:
        if int(team[0]) < Year:
            continue
        elif int(team[0]) > Year:
            continue
        elif int(team[1]) != teamNum:
            continue
        
        return float(team[2])
    
    return 0.0

def getKenRating3(teamNum, Year):
    for team in KenData:
        if int(team[0]) < Year:
            continue
        elif int(team[0]) > Year:
            continue
        elif int(team[1]) != teamNum:
            continue
        
        return float(team[3])
    
    return 0.0

def getKenRating4(teamNum, Year):
    for team in KenData:
        if int(team[0]) < Year:
            continue
        elif int(team[0]) > Year:
            continue
        elif int(team[1]) != teamNum:
            continue
        
        return float(team[4])
    
    return 0.0

    

def getTeamSeedListForYear(year):
    teamList = []
    for i in range(1, len(SeedData)):
        if int(SeedData[i][0]) < year:
            continue
        elif int(SeedData[i][0]) > year:
            break
        
        SeedString = SeedData[i][1]
        
        if len(SeedString) == 4:
            newTeam = [int(SeedData[i][2]), int(SeedString[1:len(SeedString) - 1])]
            teamList.append(newTeam.copy())
        else:
            newTeam = [int(SeedData[i][2]), int(SeedString[1:len(SeedString)])]
            teamList.append(newTeam.copy())
        
    return teamList

def getTeamSeed(teamNum, year):
    for i in range(1, len(SeedData)):
        if int(SeedData[i][0]) < year:
            continue
        elif int(SeedData[i][0]) > year:
            break
        
        SeedString = SeedData[i][2]
        
        if int(teamNum) == int(SeedData[i][1]):
            if len(SeedString) == 4:
                return int(SeedString[1:len(SeedString) - 1])
            else:
                return int(SeedString[1:len(SeedString)])
            
        
    return -1

def getGameOffRTG(teamNum, Year, Game, didWin):
    pointsScored = 0.0
    totalPossessions = 0.0
    
    if didWin:
        #IF Won Game
        pointsScored = float(Game[3])
        
        totalPossessions = float(Game[9]) + float(Game[11]) - float(Game[14]) + float(Game[17]) + float(Game[13]) / 2.0
    else:
        #IF Lost Game
        pointsScored = float(Game[5])
        
        totalPossessions = float(Game[22]) + float(Game[24]) - float(Game[27]) + float(Game[30]) + float(Game[26]) / 2.0
        
    if totalPossessions != 0.0:
        return (pointsScored / totalPossessions) * 100.0
    
def getGameDefRTG(teamNum, Year, Game, didWin):
    pointsScoredAgainst = 0.0
    totalPossessions = 0.0
    
    if didWin:
        #IF Won Game
        pointsScoredAgainst = float(Game[5])
        
        totalPossessions = float(Game[22]) + float(Game[24]) - float(Game[27]) + float(Game[30]) + float(Game[26]) / 2.0
    else:
        #IF Lost Game
        pointsScoredAgainst = float(Game[3])
        
        totalPossessions = float(Game[9]) + float(Game[11]) - float(Game[14]) + float(Game[17]) + float(Game[13]) / 2.0
        
    if totalPossessions != 0:
        return (pointsScoredAgainst / totalPossessions) * 100.0

def getGameNetRTG(teamNum, Year, Game, didWin):
    
    return pow(((getGameOffRTG(teamNum, Year, Game, didWin) - getGameDefRTG(teamNum, Year, Game, didWin)) + 5), 2)
        
def getTeamNetRTG(teamNum, Year):
    
    return 0
    totalNetRating = 0.0
    totalGamesPlayed = 0.0
    
    #Go through all games in year for team
    for i in range(1, len(RegSeasonData)):
        if int(RegSeasonData[i][0]) < Year:
            continue
        elif int(RegSeasonData[i][0]) > Year:
            break
        
        gameRating = 0.0
        
        totalGamesPlayed = totalGamesPlayed + 1.0
        
        if int(RegSeasonData[i][2]) == teamNum:
            gameRating = getGameNetRTG(teamNum, Year, RegSeasonData[i], True)
        elif int(RegSeasonData[i][4]) == teamNum:
            gameRating = getGameNetRTG(teamNum, Year, RegSeasonData[i], False)
        
        totalNetRating += gameRating
    
    if totalGamesPlayed != 0:
        return (totalNetRating / totalGamesPlayed) * 100
    
    return -1

def getTeamOffRTG(teamNum, Year):
    
    return 0
    totalNetRating = 0.0
    totalGamesPlayed = 0.0
    
    #Go through all games in year for team
    for i in range(1, len(RegSeasonData)):
        if int(RegSeasonData[i][0]) < Year:
            continue
        elif int(RegSeasonData[i][0]) > Year:
            break
        
        gameRating = 0.0
        
        totalGamesPlayed = totalGamesPlayed + 1.0
        
        if int(RegSeasonData[i][2]) == teamNum:
            gameRating = getGameOffRTG(teamNum, Year, RegSeasonData[i], True)
        elif int(RegSeasonData[i][4]) == teamNum:
            gameRating = getGameOffRTG(teamNum, Year, RegSeasonData[i], False)
        
        totalNetRating += gameRating
    
    if totalGamesPlayed != 0:
        return (totalNetRating / totalGamesPlayed) * 100
    
    return -1

def getTeamDefRTG(teamNum, Year):
    
    return 0
    totalNetRating = 0.0
    totalGamesPlayed = 0.0
    
    #Go through all games in year for team
    for i in range(1, len(RegSeasonData)):
        if int(RegSeasonData[i][0]) < Year:
            continue
        elif int(RegSeasonData[i][0]) > Year:
            break
        
        gameRating = 0.0
        
        totalGamesPlayed = totalGamesPlayed + 1.0
        
        if int(RegSeasonData[i][2]) == teamNum:
            gameRating = getGameDefRTG(teamNum, Year, RegSeasonData[i], True)
        elif int(RegSeasonData[i][4]) == teamNum:
            gameRating = getGameDefRTG(teamNum, Year, RegSeasonData[i], False)
        
        totalNetRating += gameRating
    
    if totalGamesPlayed != 0:
        return (totalNetRating / totalGamesPlayed) * 100
    
    return -1

def getAllNetRatingsForYear(Year):
    YearTeamsList = getTeamSeedListForYear(Year)
    
    for team in YearTeamsList:
        #team.append(getTeamNetRTG(team[0], Year))
        #team.append(getTeamOffRTG(team[0], Year))
        #team.append(getTeamDefRTG(team[0], Year))
        team.append(getKenRating1(team[0], Year))
        team.append(getKenRating2(team[0], Year))
        team.append(getKenRating3(team[0], Year))
        team.append(getKenRating4(team[0], Year))
        
    return YearTeamsList

def sortList(ListToSort):
    i = 0
    while i < len(ListToSort):
        if i > 0 and ListToSort[i][2] > ListToSort[i - 1][2]:
            swap(ListToSort, i, i - 1)
            
            i = i - 1
        else:
            i = i + 1
            
def getScheduleDifficulty(teamNum, Year, RatingsList, NotFoundValue, SchedMult):
    DiffSum = 0.0
    teamsFaced = 0.0
    for i in range(1, len(RegSeasonData)):
        if int(RegSeasonData[i][0]) < Year:
            continue
        elif int(RegSeasonData[i][0]) > Year:
            break
        
        if int(RegSeasonData[i][2]) == teamNum:
            DiffSum = DiffSum + findTeamRating(int(RegSeasonData[i][4]), RatingsList, NotFoundValue)
        elif int(RegSeasonData[i][4]) == teamNum:
            DiffSum = DiffSum + findTeamRating(int(RegSeasonData[i][4]), RatingsList, NotFoundValue)
        else:
            continue
        
        teamsFaced += 1.0
        
    return (DiffSum / teamsFaced) * SchedMult

def adjustForScheduleDifficulty(Year, RatingsList, NotFoundValue, SchedMult):
    for team in RatingsList:
        team[2] += getScheduleDifficulty(team[0], Year, RatingsList, NotFoundValue, SchedMult)
            
def findTeamRating(teamNum, RatingsList, NotFoundValue):
    for team in RatingsList:
        if team[0] == teamNum:
            return team[2]
            
    return NotFoundValue
            
def testOnlyNetRatings(RatingsList, Year):
    winNum = 0
    totalNum = 0
    for i in range(1, len(TourneyData)):
        if int(TourneyData[i][0]) < Year:
            continue
        elif int(TourneyData[i][0]) > Year:
            break
        
        totalNum = totalNum + 1
        
        loserNet = 0
        winnerNet = 0
        
        for team in RatingsList:
            if team[0] == TourneyData[i][2]:
                winnerNet = team[2]
                
            if team[0] == TourneyData[i][4]:
                loserNet = team[2]
                
        if winnerNet > loserNet:
            winNum = winNum + 1
            
            
    return winNum / totalNum

def getAllRatings():
    retList = []
    TestYear = 2011
    TestNotFoundValue = -5
    TestSchedMult = 0.44
    
    while TestYear < 2024:
        retList.append([TestYear, TestYear, TestYear, TestYear, TestYear, TestYear])
        RatingsList = getAllNetRatingsForYear(TestYear)
        #adjustForScheduleDifficulty(TestYear, RatingsList, TestNotFoundValue, TestSchedMult)
            
        for listThing in RatingsList:
            retList.append(listThing)
            
        print(TestYear)
        
        TestYear = TestYear + 1
        if TestYear == 2020:
            TestYear = TestYear + 1
        
    #print(retList)
    savetxt('RatingsDataRY.csv', retList, delimiter=',')
        

def getTeamRating(teamNum, Year):
    startLooking = False
    for i in range(0, len(RatingData)):
        if int(RatingData[i][1]) > Year:
            return []
        elif int(RatingData[i][1]) == Year:
            startLooking = True
            continue
        elif not startLooking:
            continue
        
        if RatingData[i][0] == teamNum:
            newRatingThing = RatingData[i].copy()
            #newRatingThing[1] = 0
            newRatingThing[2] = 0
            newRatingThing[3] = 0
            newRatingThing[4] = 0
            #newRatingThing[5] = 0
            return newRatingThing
                
    return []

def prepareData(Year):
    xTrain = []
    yTrain = []
    
    for i in range(0, len(TourneyData)):
        if int(TourneyData[i][0]) < Year:
            continue
        elif int(TourneyData[i][0]) > Year:
            break
        
        newData = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        game = TourneyData[i]
        
        teamData1 = []
        teamData2 = []
        
        if int(game[2]) < int(game[4]):
            yTrain.append(1)
            
            teamData1 = getTeamRating(int(game[2]), Year)
            teamData2 = getTeamRating(int(game[4]), Year)
            
        else:
            yTrain.append(0)
            
            teamData1 = getTeamRating(int(game[4]), Year)
            teamData2 = getTeamRating(int(game[2]), Year)
            
            
        for k in range(13):
            if k < 6:
                newData[k] = teamData1[k]
            elif k < 12:
                newData[k] = teamData2[k - 6]
            else:
                newData[k] = float(TourneyData[i][1]) - 134.0
        
        xTrain.append(newData.copy())
        
    return (xTrain, yTrain)

def prepareDataExcept(Year):
    xTrain = []
    yTrain = []
    curYear = Year
    
    for i in range(0, len(TourneyData)):
        if int(TourneyData[i][0]) < 2012:
            continue
        if int(TourneyData[i][0]) == Year or int(TourneyData[i][0]) == 2020 or int(TourneyData[i][0]) == 2021 or int(TourneyData[i][0]) == 2022:
            continue
        
        curYear = int(TourneyData[i][0])
        
        newData = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        game = TourneyData[i]
        
        teamData1 = []
        teamData2 = []
        
        if int(game[2]) < int(game[4]):
            yTrain.append(1)
            
            teamData1 = getTeamRating(int(game[2]), curYear)
            teamData2 = getTeamRating(int(game[4]), curYear)
            
        else:
            yTrain.append(0)
            
            teamData1 = getTeamRating(int(game[4]), curYear)
            teamData2 = getTeamRating(int(game[2]), curYear)
            
        for k in range(13):
            if k < 6:
                newData[k] = teamData1[k]
            elif k < 12:
                newData[k] = teamData2[k - 6]
            else:
                newData[k] = float(TourneyData[i][1]) - 134.0
        
        xTrain.append(newData.copy())
        
    return (xTrain, yTrain)
  
def BuildStartGuesses(Year):
    if Year != 2023:
        gameList = []
        for i in range(0, len(TourneyData)):
            if int(TourneyData[i][0]) < Year:
                continue
            if int(TourneyData[i][0]) > Year:
                break
            if int(TourneyData[i][1]) < 136:
                continue
            if int(TourneyData[i][1]) > 137:
                break
            
            newData = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
            game = TourneyData[i]
            
            teamData1 = getTeamRating(int(game[2]), Year)
            teamData2 = getTeamRating(int(game[4]), Year)
            
            for k in range(13):
                if k < 6:
                    newData[k] = teamData1[k]
                elif k < 12:
                    newData[k] = teamData2[k - 6]
                else:
                    newData[k] = 2
            
            gameList.append(newData.copy())
        
        return gameList.copy()
    else:
        gameList = []
        startLooking = False
        division = 0
        resetNum = 7
        for i in range(0, len(RatingData)):
            if int(RatingData[i][1]) > Year:
                return []
            elif int(RatingData[i][1]) == Year:
                startLooking = True
                continue
            elif not startLooking:
                continue
            
            curTeam = RatingData[i]
            index = int(curTeam[1])
            if index == 1 or index == 2 or index == 3 or index == 4 or index == 5 or index == 6 or index == 7 or index == 8:
                thisGame = [int(curTeam[0]), int(curTeam[1]), int(curTeam[5]), 0, 0, 0, 2]
                gameList.append(thisGame)
                resetNum = 7
            elif index == 9 or index == 10 or index == 11 or index == 12 or index == 13 or index == 14 or index == 15 or index == 16:
                #print(i - 8 - 759 + resetNum - division * 8)
                #print(gameList)
                gameList[i - 8 - 759 + resetNum - division * 8][3] = int(curTeam[0])
                gameList[i - 8 - 759 + resetNum - division * 8][4] = int(curTeam[1])
                gameList[i - 8 - 759 + resetNum - division * 8][5] = int(curTeam[5])
                resetNum = resetNum - 2
                
            if index == 16:
                division = division + 1
                
        return gameList.copy()
                
            

def getSeedArray(Year):
    seededArray = [0] * 64
    for i in range(len(SeedData)):
        team = SeedData[i]
        if int(team[0]) < Year:
            continue
        elif int(team[0]) > Year:
            break
        
        
        seedNum = 0
        SeedString = team[1]
        seedRegion = SeedString[0:1]
        if len(SeedString) == 4:
            if not checkPlayInGameResult(int(team[2]), Year):
                continue
            seedNum = int(SeedString[1:len(SeedString) - 1])
        else:
            seedNum = int(SeedString[1:len(SeedString)])
            
        tempAdj = 0
        if seedRegion == "W":
            tempAdj = 0
        elif seedRegion == "X":
            tempAdj = 1
        elif seedRegion == "Y":
            tempAdj = 2
        elif seedRegion == "Z":
            tempAdj = 3
            
        seededArray[seedNum - 1 + tempAdj * 16] = int(team[2])
        
    return seededArray

def checkPlayInGameResult(teamNum, Year):
    i = 0
    for i in range(1, len(TourneyData)):
        if int(TourneyData[i][0]) < Year:
            continue
        elif int(TourneyData[i][0]) > Year:
            break
        
        if int(TourneyData[i][2]) == teamNum:
            return True
        elif int(TourneyData[i][4]) == teamNum:
            return False

def getNextMatchHelper(seed):
    one = [8,9,4,5,12,13]
    two = [7,10,6,11,3,14]
    three = [6,11,7,10,2,15]
    four = [5,12,1,16,8,9]
    five = [4,13,1,16,8,9]
    six = [3,14,7,10,2,15]
    seven = [2,15,6,11,3,14]
    eight = [1,16,4,5,12,13]
    nine = [1,16,4,5,12,13]
    ten = [2,15,6,11,3,14]
    eleven = [3,14,7,10,2,15]
    twelve = [4,13,1,16,8,9]
    thirteen = [5,12,1,16,8,9]
    fourteen = [6,11,7,10,2,15]
    fifteen = [7,10,6,11,3,14]
    sixteen = [8,9,4,5,12,13]
    
    if seed % 16 == 0:
        return one
    elif seed % 16 == 1:
        return two
    elif seed % 16 == 2:
        return three
    elif seed % 16 == 3:
        return four
    elif seed % 16 == 4:
        return five
    elif seed % 16 == 5:
        return six
    elif seed % 16 == 6:
        return seven
    elif seed % 16 == 7:
        return eight
    elif seed % 16 == 8:
        return nine
    elif seed % 16 == 9:
        return ten
    elif seed % 16 == 10:
        return eleven
    elif seed % 16 == 11:
        return twelve
    elif seed % 16 == 12:
        return thirteen
    elif seed % 16 == 13:
        return fourteen
    elif seed % 16 == 14:
        return fifteen
    elif seed % 16 == 15:
        return sixteen
    
    return []

def getNextMatch(winTeam, SeedArray, WinTeams):
    if len(WinTeams) > 4:
        winTeamIndex = 0
        j = 0
        teamSeeds = []
        for j in range(len(WinTeams)):
            team = WinTeams[j][0]
            curTeamArray = [int(team), 0, 0]
            i = 0
            for i in range(64):
                if team == SeedArray[i]:
                    tempAdj = 0
                    if i < 16:
                        tempAdj = 0
                    elif i < 32:
                        tempAdj = 1
                    elif i < 48:
                        tempAdj = 2
                    else:
                        tempAdj = 3
                        
                    curTeamArray[1] = i % 16
                    curTeamArray[2] = tempAdj
                    
                    if int(team) == int(winTeam):
                        winTeamIndex = j
                    
                    break
            
            teamSeeds.append(curTeamArray.copy())
            
        curTeamSeedInfo = teamSeeds[winTeamIndex]
        nextPossibleMatches = getNextMatchHelper(curTeamSeedInfo[1])
        
        curClosestTeam = -1
        curLowestSeedCloseness = 1000
        i = 0
        for i in range(len(WinTeams)):
            if winTeam == teamSeeds[i][0]:
                continue
            if curTeamSeedInfo[2] == teamSeeds[i][2]:
                
                seedCloseness = 10
                
                j = 0
                for j in range(len(nextPossibleMatches)):
                    if nextPossibleMatches[j] == teamSeeds[i][1] + 1:
                        seedCloseness = j
                        break
                    
                if seedCloseness < curLowestSeedCloseness:
                    curLowestSeedCloseness = seedCloseness
                    curClosestTeam = teamSeeds[i][0]
              
        return curClosestTeam
    elif len(WinTeams) > 2:
        winTeamIndex = 0
        j = 0
        teamSeeds = []
        for j in range(len(WinTeams)):
            team = WinTeams[j][0]
            curTeamArray = [int(team), 0, 0]
            i = 0
            for i in range(64):
                if team == SeedArray[i]:
                    tempAdj = 0
                    if i < 16:
                        tempAdj = 0
                    elif i < 32:
                        tempAdj = 1
                    elif i < 48:
                        tempAdj = 2
                    else:
                        tempAdj = 3
                        
                    curTeamArray[1] = i % 16
                    curTeamArray[2] = tempAdj
                    
                    if int(team) == int(winTeam):
                        winTeamIndex = j
                    
                    break
            
            teamSeeds.append(curTeamArray.copy())
            
        curTeamSeedInfo = teamSeeds[winTeamIndex]
        i = 0
        for i in range(len(WinTeams)):
            if teamSeeds[i][0] == winTeam:
                continue
            
            if curTeamSeedInfo[2] == 0 or curTeamSeedInfo[2] == 1:
                if teamSeeds[i][2] == 0 or teamSeeds[i][2] == 1:
                    return teamSeeds[i][0]
            else:
                if teamSeeds[i][2] == 2 or teamSeeds[i][2] == 3:
                    return teamSeeds[i][0]
                
    else:
        for team in WinTeams:
            if team[0] == winTeam:
                continue
            else:
                return team[0]
            
    
    
def BuildGuesses(Model, Year):
    RoundTracker = []
    Start = BuildStartGuesses(Year)
        
    RoundTracker.append(Start)
    SeedArray = getSeedArray(Year)
    
    #Make Matches for round of 32
    roundNum = 4
    
    WinnerList = []
    for game in Start:
        predResult = Model.predict(game)
        
        winner = []
        if predResult[0] == 1:
            winner = [game[0], game[1], game[2], game[3], game[4], game[5]]
        else:
            winner = [game[6], game[7], game[8], game[9], game[10], game[11]]
            
        WinnerList.append(winner.copy())
        
    NewMatches = []
    count = 0
    for winner in WinnerList:
        shouldSkip = False
        count = count + 1
        for newMatch in NewMatches:
            if winner[0] == newMatch[0] or winner[0] == newMatch[6]:
                shouldSkip = True
                break
        if shouldSkip:
            continue
        
        curWinnerOPP = getNextMatch(winner[0], SeedArray, WinnerList)
        opponent = []
        
        for newWinner in WinnerList:
            if newWinner[0] == curWinnerOPP:
                opponent = newWinner
                break
            
        if opponent == []:
            print("1")
        NewMatches.append(makeNewMatch(winner, opponent, roundNum))
        
        
    RoundTracker.append(NewMatches.copy())
    
    #Make Matches for round of 16
    roundNum = 10
    
    Start = NewMatches.copy()
    WinnerList = []
    for game in Start:
        predResult = Model.predict(game)
        
        winner = []
        if predResult[0] == 1:
            winner = [game[0], game[1], game[2], game[3], game[4], game[5]]
        else:
            winner = [game[6], game[7], game[8], game[9], game[10], game[11]]
            
        WinnerList.append(winner.copy())
        
    NewMatches = []
    count = 0
    for winner in WinnerList:
        shouldSkip = False
        count = count + 1
        for newMatch in NewMatches:
            if winner[0] == newMatch[0] or winner[0] == newMatch[6]:
                shouldSkip = True
                break
        if shouldSkip:
            continue
        
        curWinnerOPP = getNextMatch(winner[0], SeedArray, WinnerList)
        opponent = []
        
        for newWinner in WinnerList:
            if newWinner[0] == curWinnerOPP:
                opponent = newWinner
                break
            
        if opponent == []:
            print("2")
        NewMatches.append(makeNewMatch(winner, opponent, roundNum))
        
        
    RoundTracker.append(NewMatches)
    
    #Make Matches for round of 8
    roundNum = 12
    
    Start = NewMatches.copy()
    WinnerList = []
    for game in Start:
        predResult = Model.predict(game)
        
        if predResult[0] == 1:
            winner = [game[0], game[1], game[2], game[3], game[4], game[5]]
        else:
            winner = [game[6], game[7], game[8], game[9], game[10], game[11]]
            
        WinnerList.append(winner.copy())
        
    NewMatches = []
    count = 0
    for winner in WinnerList:
        shouldSkip = False
        count = count + 1
        for newMatch in NewMatches:
            if winner[0] == newMatch[0] or winner[0] == newMatch[6]:
                shouldSkip = True
                break
        if shouldSkip:
            continue
        
        curWinnerOPP = getNextMatch(winner[0], SeedArray, WinnerList)
        opponent = []
        
        for newWinner in WinnerList:
            if newWinner[0] == curWinnerOPP:
                opponent = newWinner
                break
            
        if opponent == []:
            print("3")
        NewMatches.append(makeNewMatch(winner, opponent, roundNum))
        
        
    RoundTracker.append(NewMatches)
    
    #Make Matches for round of 4
    roundNum = 18
    
    Start = NewMatches.copy()
    WinnerList = []
    for game in Start:
        predResult = Model.predict(game)
        
        if predResult[0] == 1:
            winner = [game[0], game[1], game[2], game[3], game[4], game[5]]
        else:
            winner = [game[6], game[7], game[8], game[9], game[10], game[11]]
            
        WinnerList.append(winner.copy())
        
    NewMatches = []
    count = 0
    for winner in WinnerList:
        shouldSkip = False
        count = count + 1
        for newMatch in NewMatches:
            if winner[0] == newMatch[0] or winner[0] == newMatch[6]:
                shouldSkip = True
                break
        if shouldSkip:
            continue
        
        curWinnerOPP = getNextMatch(winner[0], SeedArray, WinnerList)
        opponent = []
        
        for newWinner in WinnerList:
            if newWinner[0] == curWinnerOPP:
                opponent = newWinner
                break
            
        if opponent == []:
            print("4")
        NewMatches.append(makeNewMatch(winner, opponent, roundNum))
        
        
    RoundTracker.append(NewMatches)
    
    #Make Matches for finals
    roundNum = 20
    
    Start = NewMatches.copy()
    WinnerList = []
    for game in Start:
        predResult = Model.predict(game)
        
        if predResult[0] == 1:
            winner = [game[0], game[1], game[2], game[3], game[4], game[5]]
        else:
            winner = [game[6], game[7], game[8], game[9], game[10], game[11]]
            
        WinnerList.append(winner.copy())
        
    NewMatches = []
    count = 0
    for winner in WinnerList:
        shouldSkip = False
        count = count + 1
        for newMatch in NewMatches:
            if winner[0] == newMatch[0] or winner[0] == newMatch[6]:
                shouldSkip = True
                break
        if shouldSkip:
            continue
        
        curWinnerOPP = getNextMatch(winner[0], SeedArray, WinnerList)
        opponent = []
        
        for newWinner in WinnerList:
            if newWinner[0] == curWinnerOPP:
                opponent = newWinner
                break
        if opponent == []:
            print("5")
        NewMatches.append(makeNewMatch(winner, opponent, roundNum))
        
        
    RoundTracker.append(NewMatches)
    
    #Make Final Winner
    roundNum = 100
    
    Start = NewMatches.copy()
    WinnerList = []
    for game in Start:
        predResult = Model.predict(game)
        
        if predResult[0] == 1:
            winner = [game[0], game[1], game[2], game[3], game[4], game[5]]
        else:
            winner = [game[6], game[7], game[8], game[9], game[10], game[11]]
            
        WinnerList.append(winner.copy())
        
    NewMatches = []
    count = 0
    for winner in WinnerList:
        NewMatches.append(makeNewMatch(winner, winner.copy(), roundNum))
        
    RoundTracker.append(NewMatches)
    
    return RoundTracker
        
def makeNewMatch(Team1, Team2, roundNum):
    newData = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    
    for k in range(13):
        if k < 6:
            newData[k] = Team1[k]
        elif k < 12:
            newData[k] = Team2[k - 6]
        else:
            newData[k] = roundNum
    
    return newData
    
def getBracketScore(GuessTree, Year):
    i = 0
    score = 0
    for i in range(len(TourneyData)):
        match = TourneyData[i]
        if int(match[0]) < Year:
            continue
        elif int(match[0]) > Year:
            break
        
        if int(match[1]) < 138:
            continue
        elif int(match[1]) < 143:
            for guessMatch in GuessTree[1]:
                if int(match[2]) == guessMatch[0] or int(match[2]) == guessMatch[6]:
                    score = score + 1
                if int(match[4]) == guessMatch[0] or int(match[4]) == guessMatch[6]:
                    score = score + 1
        elif int(match[1]) < 145:
            for guessMatch in GuessTree[2]:
                if int(match[2]) == guessMatch[0] or int(match[2]) == guessMatch[6]:
                    score = score + 2
                if int(match[4]) == guessMatch[0] or int(match[4]) == guessMatch[6]:
                    score = score + 2
        elif int(match[1]) < 152:
            for guessMatch in GuessTree[3]:
                if int(match[2]) == guessMatch[0] or int(match[2]) == guessMatch[6]:
                    score = score + 4
                if int(match[4]) == guessMatch[0] or int(match[4]) == guessMatch[6]:
                    score = score + 4
        elif int(match[1]) < 154:
            for guessMatch in GuessTree[4]:
                if int(match[2]) == guessMatch[0] or int(match[2]) == guessMatch[6]:
                    score = score + 8
                if int(match[4]) == guessMatch[0] or int(match[4]) == guessMatch[6]:
                    score = score + 8
        elif int(match[1]) == 154:
            for guessMatch in GuessTree[5]:
                if int(match[2]) == guessMatch[0] or int(match[2]) == guessMatch[6]:
                    score = score + 16
                if int(match[4]) == guessMatch[0] or int(match[4]) == guessMatch[6]:
                    score = score + 16
                    
            for guessMatch in GuessTree[5]:
                if int(match[2]) == guessMatch[0]:
                    score = score + 32
                
    return score
                  
      
def TestData(Year):
    (xTrain, yTrain) = prepareDataExcept(Year)
    (xTest, yTest) = prepareData(Year)
    
    model = GradientBoostingClassifier(criterion='friedman_mse', init=None,
                  learning_rate=0.5, loss='deviance', max_depth=3,
                  max_features=None, max_leaf_nodes=None,
                  min_samples_leaf=1,
                  min_weight_fraction_leaf=0.0,
                  n_estimators=4, presort='auto', random_state=0,
                  subsample=1.0, verbose=0, warm_start=False)
    
    # Fit the model
    model.fit(xTrain, yTrain)
    
    import warnings
    warnings.filterwarnings('ignore')
    
    score_te = model.score(xTest, yTest)
    
    GuessTree = BuildGuesses(model, Year)
    
    thisScore = getBracketScore(GuessTree, Year)
        
    
    
    return (score_te, thisScore)

def PredictForYear(Year):
    (xTrain, yTrain) = prepareDataExcept(Year)
    
    model = GradientBoostingClassifier(criterion='friedman_mse', init=None,
                  learning_rate=0.85, loss='deviance', max_depth=3,
                  max_features=None, max_leaf_nodes=None,
                  min_samples_leaf=1,
                  min_weight_fraction_leaf=0.0,
                  n_estimators=5, presort='auto', random_state=0,
                  subsample=1.0, verbose=0, warm_start=False)
    
    # Fit the model
    model.fit(xTrain, yTrain)
    
    import warnings
    warnings.filterwarnings('ignore')
    
    GuessTree = BuildGuesses(model, Year)
    
    return GuessTree


   
#getAllRatings()

accuracyArray = []
scoreArray = []

scoreNet = 0.0
netthing = 0.0
num = 0.0
for i in range (2012, 2023):
    if i == 2020 or i == 2021 or i == 2022 or i == 2023:
        continue
    print(i)
    (score_te, thisScore) = TestData(i)
    netthing += score_te
    scoreNet += thisScore
    accuracyArray.append(score_te)
    scoreArray.append(thisScore)
    
    num += 1.0

print("Accuracy")
print(netthing / num)
print(accuracyArray)

print("Score")
print(scoreNet / num)
print(scoreArray)


"""


print("2023 Bracket")
goodBracket = PredictForYear(2023)
for curRound in goodBracket:
    if curRound[0][6] == 2:
        continue
    for match in curRound:
        team1 = int(match[0])
        team1N = ""
        team2 = int(match[3])
        team2N = ""
        
        for team in TeamsData:
            if int(team[0]) == team1:
                team1N = team[1]
            if int(team[0]) == team2:
                team2N = team[1]
                
        print(team1N, "versus", team2N)


"""





