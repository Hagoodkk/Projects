Compile Instruction:
Open command line in SNAProj/src/
Input in command line: 'javac -classpath DIRECTORY_PATH\SNAProj\lib\* *.java'

Run Instruction:
Open command line in SNAProj/src/
Input in command line: 'java -cp DIRECTORY_PATH\SNAProj\lib\*;. SNAPProj CSV_FILE_DIRECTORY TEST_NUM'

<!--------------------------------------------------------------------------------------------------------------------->

DIRECTORY_PATH is the full path directory to where SNAProj is located
CSV_FILE_DIRECTORY is the full path directory of the .csv file you wish to analyze
TEST_NUM is an integer representing the number of the test you are running. It will create a file ending in TEST_NUM,
and will overwrite any previous tests run on the same .csv file name with the same TEST_NUM.

NOTE: This program generates test files containing information for every iteration. With large graph files, this means
      that a lot of hard disk memory can be used up very quickly.