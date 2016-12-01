import pandas as pd
import numpy as np
import operator
import math

maxLimit = 5000000
demand = pd.read_csv("demand.csv")
habit = pd.read_csv("habit.csv")
reach = pd.read_csv("reach.csv")
print habit
segment = int(raw_input("Enter Segment Number: "))
print "0:Budet 1:Target Reach 2:Entire"
mdb = int(raw_input("Make decision by: "))
if mdb==1:
	targetLW = float(raw_input("Target Reach(Lower Limit): "))
	targetUP = float(raw_input("Target Reach(Upper Limit): "))
elif mdb==0:
	bugetLW = int(raw_input("Buget(Lower Limit): "))
	bugetUP = int(raw_input("Buget(Upper Limit): "))
print "0:Plan 1:Total Cost 2:Total Reach 3:Unite Gain"
sortBy = raw_input("Enter Sort Number: ")
if sortBy == "":
	sortBy=3
else:
	sortBy = int(sortBy)
outPut = raw_input("Enter output file name: ")
if outPut == "":
	outPut = "result"

# Pre-process data
for i in range(0,habit.shape[0]):
	for j in range(1,4):
		habit.ix[i,j]=float(habit.ix[i,j].strip('%'))/100

for i in range(0,reach.shape[0]):
	reach.ix[i,0] = reach.ix[i,0].strip('$').replace(',','')
	for j in range(1,3):
		reach.ix[i,j]=float(reach.ix[i,j].strip('%'))/100
print "Processing..."

result = {}
count = 0
for i in range(0,reach.shape[0]):
	for j in range(0,reach.shape[0]):
		for k in range(0,reach.shape[0]):
			plan = "TV:"+reach.ix[i,0]+" Intelnet:"+reach.ix[j,0]+" Magazines:"+reach.ix[k,0]
			totalCost = float(reach.ix[i,0])+float(reach.ix[j,0])+float(reach.ix[k,0])
			totalReach = reach.ix[i,1]*habit.ix[segment,1]+reach.ix[j,2]*habit.ix[segment,2]+reach.ix[k,1]*habit.ix[segment,3]
			if totalCost != 0:
				unitGain = totalReach/totalCost*100000000
			else:
				unitGain = 0
			#if totalCost <= maxLimit:
			if (mdb==0 and totalCost>=bugetLW and totalCost<=bugetUP) or (mdb==1 and totalReach>=targetLW and totalReach<=targetUP) or (mdb>=2):
				result[count]={}
				result[count]["plan"]=plan
				result[count]["totalCost"]=totalCost
				result[count]["totalReach"]=totalReach
				result[count]["unitGain"]=unitGain
				count += 1
resultdf = pd.DataFrame.from_dict(result)
resultdf = resultdf.transpose()
columnName = {0:"plan",1:"totalCost",2:"totalReach",3:"unitGain"}
resultdf = resultdf.sort_values(columnName[sortBy],ascending=False)
labels = ["Plan", "Total Cost", "Total Reach", "Unit Gain"]
resultdf.columns = labels
resultdf.to_csv(outPut+".csv")

print "Done! The output file " + outPut + ".csv is generated"
