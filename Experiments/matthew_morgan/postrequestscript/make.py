# import csv

# with open(r'C:\Users\mattm\Downloads\megaGymDataset.csv', 'r', encoding='utf-8') as firstFile, open('./second.txt','w') as secondFile:
#     csv_reader = csv.reader(firstFile)
#     i = 0
#     for row in csv_reader:
#         newrow = []
#         for item in row:
#             if item == "":
#                 newrow.append("null")
#             else:
#                 newrow.append(item)
#         secondFile.write(f'workoutRepository.save(new User({newrow[0]},"{newrow[1]}", "{newrow[2]}","{newrow[3]}","{newrow[4]}","{newrow[5]}","{newrow[6]}"));\n')
#         print(i)
#         i+=1

# import csv

# def fixQuotes(s):
#     return s.replace(r'"', r'\"')

# with open(r'C:\Users\mattm\Downloads\megaGymDataset.csv', 'r', encoding='utf-8') as firstFile, open('./second.txt','w') as secondFile:
#     csv_reader = csv.reader(firstFile)

#     for row in csv_reader:
#         s = "workoutRepository.save(new Workout("
#         i = 0
#         for item in row[:7]:
#             if item == "":
#                 s+= "null,"
#             else:
#                 if item.find(r'"') >=0:
#                     print(item)
#                     item = fixQuotes(item)
#                     print(item)

#                 if i ==0:
#                     s+= f'{item},'
#                 else:
#                     s+= f'"{item}",'
#             i+=1
#         s = s[:-1]
#         secondFile.write(s+"));\n")


import csv

def fixQuotes(s):
    return s.replace(r'"', r'\"')

with open(r'C:\Users\mattm\Downloads\megaGymDataset.csv', 'r', encoding='utf-8') as firstFile, open('./second.txt','w') as secondFile:
    csv_reader = csv.reader(firstFile)
    i = 0
    for row in csv_reader:
        s = f"Workout work{i} = new Workout("
        
        j = 0
        for item in row[:7]:
            if item == "":
                s+= "null,"
            else:
                if item.find(r'"') >=0:
                    print(item)
                    item = fixQuotes(item)
                    print(item)

                if j ==0:
                    s+= f'{item},'
                else:
                    s+= f'"{item}",'
            j+=1
        s = s[:-1]
        if(row[7]!=""):
            secondFile.write(s+");\n")
            secondFile.write(f"workoutRepository.save(work{i});\n")
        i+=1



