var getTheWin = false;
var stopTheWin = false;

// Moves on the defense
var stop3SingleBlock = false;


// Moves on the Offense
var setUpTTP = false;
var setUpStraightFive = false;
var setUpMidleFive = false;
var setUpMonicasFive = false;

var startBoardLayout = [];

var startBlock0 = [];
var startBlock1 = [];
var startBlock2 = [];
var startBlock3 = [];

var allPossibleBoards = [];

var count = 0;


//
//
// ********** FUNCTIONS **********

function findAllPosibleMoves(currentBoardLayout){
	for(i = 0 ; i < 4 ; i++){
		for(x = 0 ; x < 9 ; x++){
			if(currentBoardLayout[i][x].backgroundImage == 'images/hole.png' || currentBoardLayout[i][x].backgroundImage == undefined){
				if(currentBoardLayout[i][x].backgroundImage == undefined){
					currentBoardLayout[i][x].backgroundImage = 'images/hole.png';
				}
				startBoardLayout.push({view: currentBoardLayout[i][x], possibleMove: true, blockNumber: i});
			}else{
				startBoardLayout.push({view: currentBoardLayout[i][x], possibleMove: false, blockNumber: i});
			}	
		}
	}
	
	for(z = 0 ; z < startBoardLayout.length ; z++){
		if(startBoardLayout[z].blockNumber == 0){
			startBlock0.push(startBoardLayout[z]);
		}else if(startBoardLayout[z].blockNumber == 1){
			startBlock1.push(startBoardLayout[z]);
		}else if(startBoardLayout[z].blockNumber == 2){
			startBlock2.push(startBoardLayout[z]);
		}else if(startBoardLayout[z].blockNumber == 3){
			startBlock3.push(startBoardLayout[z]);
		}
	}
	
	allPossibleBoards[0] = updateBlock(startBlock0, 'right', 0);
	allPossibleBoards[1] = updateBlock(startBlock0, 'left', 0);
	allPossibleBoards[2] = updateBlock(startBlock1, 'right', 9);
	allPossibleBoards[3] = updateBlock(startBlock1, 'left', 9);
	allPossibleBoards[4] = updateBlock(startBlock1, 'right', 18);
	allPossibleBoards[5] = updateBlock(startBlock1, 'left', 18);
	allPossibleBoards[6] = updateBlock(startBlock1, 'right', 27);
	allPossibleBoards[7] = updateBlock(startBlock1, 'left', 27);
	
	for(k = 0 ; k < 8 ; k++){
		for(y = 0 ; y < allPossibleBoards[0].length ; y++){
			Ti.API.info('allPossibleBoards: ' + k + ' index ' + y + ': ' + allPossibleBoards[k][y].view.backgroundImage + ' Possible move: ' + allPossibleBoards[k][y].possibleMove);
		}
	}
	
	
}

function updateBlock(block, direction, cpAdjust){
	
	var newGamePlan = [];
	var count = 0;
	
	if(direction == 'right'){
		var cp = 6 + cpAdjust;
		
		for(x = 0 ; x < 9 ; x++){
			
			if(cp == -3 + cpAdjust){
				cp = 7 + cpAdjust;	
			}
			
			if(cp == -2 + cpAdjust){
				cp = 8 + cpAdjust;	
			}
			
			block[x] = startBoardLayout[cp];		
		
			cp = cp - 3;
			
		}
	}
	
	else if(direction == 'left'){
		
		var cp = 2 + cpAdjust;
		
		for(x = 0 ; x < 9 ; x++){
			
			if(cp == 11 + cpAdjust){
				cp = 1 + cpAdjust;	
			}
			
			if(cp == 10 + cpAdjust){
				cp = 0 + cpAdjust;	
			}
			
			block[x] = startBoardLayout[cp];
			
			cp = cp + 3;
		}
		
	}	
		
	if(cpAdjust == 0){
		for(y = 0 ; y < startBoardLayout.length ; y++){
			if(y < 9){
				newGamePlan[y] = block[y];
			}else{
				newGamePlan[y] = startBoardLayout[y];
			}
		}	
	}
	
	if(cpAdjust == 9){
		for(y = 0 ; y < startBoardLayout.length ; y++){
			if(y > 8 && y < 18){
				newGamePlan[y] = block[count];
				count++;
			}else{
				newGamePlan[y] = startBoardLayout[y];
			}
		}
	}
	
	if(cpAdjust == 18){
		for(y = 0 ; y < startBoardLayout.length ; y++){
			if(y > 17 && y < 26){
				newGamePlan[y] = block[count];
				count++;
			}else{
				newGamePlan[y] = startBoardLayout[y];
			}
		}
	}
	
	if(cpAdjust == 27){
		for(y = 0 ; y < startBoardLayout.length ; y++){
			if(y > 26){
				newGamePlan[y] = block[count];
				count++;
			}else{
				newGamePlan[y] = startBoardLayout[y];
			}
		}
	}
		
	return newGamePlan; // return entire gameBoard with updated block
	
}



function evaluateAllPossibleMoves(board){
	for(i = 0 ; i < board.length ; i++){
		if(board[i].possibleMove = true){
			//
			//
			//
			// In Here... Run if statements on placement and give points depending on which are true! Assign points to move!
			//
			//
			//
		}
	}
}



function excuteBestMove(){
	
}