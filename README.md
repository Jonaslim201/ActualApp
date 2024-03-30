# IMPORTANT CLASSES:


## User class
Class with static fields, used throughout the whole app session.
![[Pasted image 20240331010034.png]]

## Firestore
Class with a static Firestore instance. All functions regarding Firestore access is all through here.
Also creates the User object with the static field.
![[Pasted image 20240331010148.png]]

## Exercise
Each individual exercise is an instance of Class of this. So Crunches and Curls are 2 different instances of the Class.
![[Pasted image 20240331010242.png]]

## Workout
Workout is a subclass of Exercise, each Workout the User has done is an instance of this Class.
![[Pasted image 20240331010426.png]]