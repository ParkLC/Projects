# Deadwood

Compile syntax: javac Deadwood.java

Run syntax: java Deadwood x

x is the number of players, 2-8 inclusive

**Legal operations during gameplay**

```who```            prints current player

```where```          prints location of current player

```role```           prints role of current player

```room options```   prints all legal moves to adjacent rooms

```role options```   pinrts all available roles

```move-{room}```    moves the current player to room specified by replacing room name with {room}

```work-{role}```    assigns player to the specified role by replacing role name with {role}

```act```            when player is assigned to a role, act sees if the player is successful in acting

```rehearse```       when player is assigned to a role, rehearse adds a practice chip to the player

```upgrade-{type}-{rank}``` when player is in casting office, player can upgrade their role, specifing payment type by replacing type with $ for dollars and c for credits, and rank by typing a number 2-6

```score```          prints the dollars, credits, rank and total score of the current player

Note: any other input will print out the possible commands
