# SpudTicket

## Description
A Minecraft plugin which is used for tickets.

## Installation

1. Download the least release and put it into the "plugins" folder
2. Run the server
3. Use /linkdb in game to link MySql
4. Edit config.yml to change other behaviors of the plugin

## Sign formatation

1. About the vendor machine:
- First line: [ticket]
- Second line: Get card: Get your spud++!; charge: Charge your spud++!; check balance：Check money left
2. About the gate:
- First line: [gate]
- Second line: Station number ("line serial:station serial", eg: "1.01")
- Third line: Direction, structure: put a block behind the sign, and place oak fence gate in its left or right. left: <-; right: ->
- Forth line: Operation, Get in: in; Get out：out

## Hints

1. Please use /linkdb only as the username and the password of the database is stored encrypted.
2. Use the signs by left click, if you need to destroy it, please run /breaksign, and /donebreaksign after finished.
3. We use "ticket" table to store the information of the card, and "deals" to store the uses of the card (get in, get out, charge).
4. The usage of the sign:
5. About build versions (from v1.1):
   (1)min: Only include compiled files of the plugin.
   (2)complete: include compiled files of both the plugin and the md5 algorithm.(use this when min version does not work)

## Problem known

1. The charging system will genarate commands with unrecognizable suffix.

## Features

1. Give card out automatically
2. Automatically pay while getting out
3. Limit the player to get in if the balance is below 10
3. Use Vault to manage the balance

## Todo

1. Different cost to different distance 
2. Refund
3. Single journey ticket
4. Automatically remove cards which dose not have any operation of a period of time and have a balance of 0

## Contribution

1.  Fork the repository
2.  Commit your code
3.  Create Pull Request