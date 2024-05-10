#include <stdio.h>
#include <semaphore.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#define TRUE		1

sem_t professorReady;			// Binary  : 1 - Professor Ready
sem_t studentDone;				// Binary  : 1 - Student is done talking
sem_t accessHallSeats;			// Binary  : 1 - Access granted to numberFreeHallSeats
sem_t studentReady;				// Counting: # of students waiting
int   numberFreeHallSeats = 0;	// Number of seats in the waiting room

// Instructions for a student process
typedef struct {
   int student_id;
   int discussion_length;
} session;

// Simulation Instructions
typedef struct {
   int num_chairs;
   int num_students;
   int min_arrival_time;
   int max_arrival_time;
   int min_discussion_length;
   int max_discussion_length;
} simulation;

// Forward declarations
void *professor(void *);
void *student(void *);
void init_random();
int get_random(int, int);
void sleep_random(int, int);
void read_params(simulation *);


void main() {
   printf("\n\t\t ACO350 - Jack Sharkey   \n");
	printf(" \t\t   Advisement\n\n");
   
   // Initialize the random seed for the simulation
   init_random();

   // Initiaize the semaphores
   sem_init(&professorReady, 0, 0);
   sem_init(&studentDone,    0, 0);
   sem_init(&accessHallSeats,  0, 1);
   sem_init(&studentReady,   0, 0);

   // Read the simulation parameters
   simulation sim;
   read_params(&sim);

   numberFreeHallSeats = sim.num_chairs;

   // Create the Professor
   pthread_t professor_tid;
   pthread_attr_t attr;
   pthread_attr_init(&attr);
   pthread_create(&professor_tid, &attr, professor, NULL);

   // Create the students, in a staggered manner
   pthread_t student_tids[sim.num_students];
   for(int i = 0; i < sim.num_students; i++) {
      session *s = (session *) malloc(sizeof(session)); 
      s->student_id = i+1;
      s->discussion_length = get_random(
         sim.min_discussion_length, 
         sim.max_discussion_length);

      pthread_create(&student_tids[i], &attr, student, s);

      sleep_random(sim.min_arrival_time, sim.max_arrival_time);
   }

   // Wait for all of the studets to complete before exiting
   for(int i = 0; i < sim.num_students; i++) {
      pthread_join(student_tids[i], NULL);
   }
}

void *professor(void *param) {
   printf("Professor: In the office\n");

   while (TRUE) {
	//Wait for the student to be ready
	sem_wait(&studentReady);
	//Safely increase the numberFreeHallSeats
	sem_wait(&accessHallSeats);
	++numberFreeHallSeats;
	printf("\t\t\t\tFree Seats: %d\n", numberFreeHallSeats);
	sem_post(&accessHallSeats);
	//Professor is now ready
	sem_post(&professorReady);
	printf("Professor: Talking\n");
	//Wait for student to be done talking
	sem_wait(&studentDone);
	printf("Professor: Done\n");
   }
}

void *student(void *param) {
   session *x = (session *)param;
   printf("Student %d: Arrived (%.2f s.)\n", 
      x->student_id, (float) x->discussion_length / 1000000.0);

   while (TRUE) {
	// Safely check the numberFreeHallSeats
	sem_wait(&accessHallSeats);
	if(numberFreeHallSeats > 0) {
		--numberFreeHallSeats;
		printf("\t\t\t\tFree Seats: %d\n", numberFreeHallSeats);
	}
	else {
		printf("Student %d: Leaving... no seats!\n", x->student_id);
		sem_post(&accessHallSeats);
		pthread_exit(NULL);
		}
	sem_post(&accessHallSeats);
	//Student is ready
	sem_post(&studentReady);
	//Wait for the professor to be ready
	sem_wait(&professorReady);
	//Student talking...
	printf("Student %d: Talking\n", x->student_id);
	usleep(x->discussion_length);
	//Student is Done
	sem_post(&studentDone);
	printf("Student %d: Done\n", x->student_id);
	pthread_exit(NULL);
   }
}

/*********************************************************
*** Service functions for random and simulation set up ***
*********************************************************/

void init_random() {
   time_t t;
   srand((unsigned) time(&t));
}

int get_random(int min, int max) {
   return (int)(rand() % (max - min)) + min;
}

void sleep_random(int min, int max) {
   usleep(get_random(min, max));
}

void read_params(simulation *s) {
   char *title = NULL;
   size_t len;

   getline(&title, &len, stdin);
   scanf("%d", &(s->num_chairs));
   scanf("%d", &(s->num_students));
   scanf("%d", &(s->min_arrival_time));
   scanf("%d", &(s->max_arrival_time));
   scanf("%d", &(s->min_discussion_length));
   scanf("%d", &(s->max_discussion_length));

   printf("Simulation: %s", title);
   printf("   Chairs    : %d\n", s->num_chairs);
   printf("   Students  : %d\n", s->num_students);
   printf("   Arrival   : %.2f - %.2f\n", 
      (float) s->min_arrival_time/1000000.0, (float) s->max_arrival_time/1000000.0);
   printf("   Discussion: %.2f - %.2f\n\n", 
      (float) s->min_discussion_length/1000000.0, (float) s->max_discussion_length/1000000.0);
}
