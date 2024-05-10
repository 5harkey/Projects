#include <stdio.h>
#include <time.h>
#include <sys/sysinfo.h>

int main(int argc, char *argv[]) {
	printf("*****************************\n");
	printf("*** ACO350 - Jack Sharkey   ***\n");
	printf("***  System Info Utility  ***\n");
	printf("*****************************\n\n");
	//Getting Date and Time
	time_t t = time(NULL);
	struct tm date = *localtime(&t);
	char *weekday;
	char *month;
	
	switch(date.tm_wday){
		case 0: weekday = "Sunday"; break;
		case 1: weekday = "Monday"; break;
		case 2: weekday = "Tuesday"; break;
		case 3: weekday = "Wednesday"; break;
		case 4: weekday = "Thursday"; break;
		case 5: weekday = "Friday"; break;
		case 6: weekday = "Saturday"; break;
	}
	switch(date.tm_mon){
		case 0: month = "January"; break;
		case 1: month = "Febuary"; break;
		case 2: month = "March"; break;
		case 3: month = "April"; break;
		case 4: month = "May"; break;
		case 5: month = "June"; break;
		case 6: month = "July"; break;
		case 7: month = "August"; break;
		case 8: month = "September"; break;
		case 9: month = "October"; break;
		case 10: month = "November"; break;
		case 11: month = "December"; break;
	}
	//Getting Time since Last Reboot
	struct sysinfo info;
	sysinfo(&info);
	long secondsSinceReboot = info.uptime;
	double daysSinceReboot = (double)secondsSinceReboot/86400;
	
	//Getting Process Information
	int numConfigProc = get_nprocs_conf();
	int numAvailProc = get_nprocs();
	short numCurrProc = info.procs;
	
	//Getting Memory Information
	double totalMemSize = (double) info.totalram;
	double availMemSize = (double) info.freeram;
	double sharedMem = (double) info.sharedram;
	double bufferMem = (double) info.bufferram;
	double totalSwap = (double) info.totalswap;
	double availSwap = (double) info.freeswap;
	
	//Printing to Screen
	printf("Current Date: %s, %s %d, %d\n", weekday, month, date.tm_mday, date.tm_year + 1900);
	printf("Current Time: %d:%02d:%02d\n", date.tm_hour, date.tm_min, date.tm_sec);
	printf("Last Reboot : %ld seconds (%.2lf days)\n\n", secondsSinceReboot, daysSinceReboot);
	
	printf("Number of processors confugured: %d\n", numConfigProc);
	printf("Number of processors available : %d\n", numAvailProc);
	printf("Number of current processes    : %hu\n\n", numCurrProc);
	
	printf("Total usable memory size: %.03lf GB\n", totalMemSize/1000000000);
	printf("Available memory size   : %.03lf GB\n", availMemSize/1000000000);
	printf("Amount of shared memory : %.02lf MB\n", sharedMem/1000000);
	printf("Memory used by buffers  : %.02lf MB\n", bufferMem/1000000);
	printf("Total swap space size   : %.02lf MB\n", totalSwap/1000000);
	printf("Swap space available    : %.02lf MB\n", availSwap/1000000);
   return 0;
}