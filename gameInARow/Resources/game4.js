//
//
// ********** VARIABLES **********
var win = Ti.UI.currentWindow;

Ti.include('appGlobal.js');

var iOS7 = isiOS7Plus();
var theTop = iOS7 ? 20 : 0;

win.top = theTop;

var customFont = "Chalkboard SE";

var miniColor1;
var miniColor2;

var player1Color = Ti.App.Properties.getString('currentColor1');

if(player1Color == null){
	player1Color = 'images/whitePlayer.png';
	miniColor1 = 'imagesMiniColors/whitePlayer.png';
}else{
	miniColor1 = Ti.App.Properties.getString('currentColor1').replace('images/', 'imagesMiniColors/');
}

var player2Color = Ti.App.Properties.getString('currentColor2');

if(player2Color == null){
	player2Color = 'images/blackPlayer.png';
	miniColor2 = 'imagesMiniColors/blackPlayer.png';
}else{
	miniColor2 = Ti.App.Properties.getString('currentColor2').replace('images/', 'imagesMiniColors/');
}




var blocks = [];
var currPos = [];

var currentAction = 0;
var currentPlayer = 1;
var higlightedBlock = '';
var lastChoosen = '';
var enableToChange = false;
var startLocation;

var player1Won = false;
var player2Won = false;

var startRotation = 0;
var rotationIsNew = false;

var moveCount = 0;

var soundEffects = ['marblePlaced.wav', 'blockRotate.wav', 'winner.wav', 'error.wav'];
var playerArray = [];

for(var i = 0 ; i < soundEffects.length ; i++){
	playerArray[i] = Ti.Media.createSound({
		url: 'sounds/' + soundEffects[i],
		volume: 0.5
	});
}

//
//
// ********** FUNCTIONS **********
function soundMarblePlaced(){
	if(Ti.App.Properties.getString('sound') == 1 || Ti.App.Properties.getString('sound') == null){
		playerArray[0].play();
	}else{
		return;
	}
}

function soundBlockRotate(){
	if(Ti.App.Properties.getString('sound') == 1 || Ti.App.Properties.getString('sound') == null){
		playerArray[1].play();
	}else{
		return;
	}
}

function soundWinner(){
	if(Ti.App.Properties.getString('sound') == 1 || Ti.App.Properties.getString('sound') == null){
		playerArray[2].play();
	}else{
		return;
	}
}

function soundError(){
	if(Ti.App.Properties.getString('sound') == 1 || Ti.App.Properties.getString('sound') == null){
		playerArray[3].play();
	}else{
		return;
	}
}


function chooseHole(blockIndex, holeIndex){
	choosen = blocks[blockIndex][holeIndex];	
	
	if(choosen.backgroundImage != 'images/hole.png'){
		soundError();
		Ti.UI.createAlertDialog({title: 'Ooops!', message: 'Occupied! Pick a new spot!' }).show();
		return;
	}
	
	soundMarblePlaced();
	
	if(lastChoosen != choosen){
		lastChoosen.backgroundImage = 'images/hole.png';
	}		
	
	if(currentPlayer == 1)
		choosen.backgroundImage = player1Color;
	else
		choosen.backgroundImage = player2Color;
	
	lastChoosen = choosen;
	
	currPos[blockIndex][holeIndex] = lastChoosen;
	
	checkForWinner();
}

function chooseBlock(index){
	if(enableToChange){
		soundError();
		Ti.UI.createAlertDialog({title: 'Ooops!', message: 'You have already rotated a block. You are not allowed to switch' }).show();
		return;
	}
	
	soundMarblePlaced();
	
	higlightedBlock = blocks[index];
	 
	for(i = 0 ; i < 4 ; i++)
		blocks[i].backgroundImage = 'images/block.png';
	
	higlightedBlock.backgroundImage = 'images/selectedBlock.png';
}

function rotateBlock(rightOrLeft){
	if(rightOrLeft == 'left'){ 		
	
		if(!enableToChange){
			startRotation = higlightedBlock.currentRotation;
		}
		
		if(higlightedBlock.currentRotation < startRotation){
			soundError();
			Ti.UI.createAlertDialog({title: 'Ooops!', message: 'You can only rotate 90 degrees in either direction' }).show();
			return;
		}
		
		soundBlockRotate();
		
		enableToChange = true;
		
		if(higlightedBlock.currentRotation == -360){
			higlightedBlock.currentRotation = 0;
		}
		
		higlightedBlock.currentRotation = higlightedBlock.currentRotation - 90;
			
		var rotateLeft90 = Ti.UI.create2DMatrix();
		rotateLeft90 = rotateLeft90.rotate(higlightedBlock.currentRotation); // in degrees
		
		var animationBlockLeft90 = Ti.UI.createAnimation({
		    transform: rotateLeft90,
		    duration: 1000,
		});
		
		higlightedBlock.animate(animationBlockLeft90);
		
		changeCurrentPosition(higlightedBlock, higlightedBlock.currentRotation);
		
		if(higlightedBlock.currentRotation == startRotation){
			rotationIsNew = false;
		}else{
			rotationIsNew = true;
		}
		
		checkForWinner();
			
	}else{	
		
		if(!enableToChange){
			startRotation = higlightedBlock.currentRotation;
		}
		
		if(higlightedBlock.currentRotation > startRotation){
			soundError();
			Ti.UI.createAlertDialog({title: 'Ooops!', message: 'You can only rotate 90 degrees in either direction' }).show();
			return;
		}
		
		soundBlockRotate();
		
		enableToChange = true;
		
		if(higlightedBlock.currentRotation == 360){
			higlightedBlock.currentRotation = 0;
		}
		
		higlightedBlock.currentRotation = higlightedBlock.currentRotation + 90;  
			
		var rotateRight90 = Ti.UI.create2DMatrix();
		rotateRight90 = rotateRight90.rotate(higlightedBlock.currentRotation); // in degrees
		
		var animationBlockRight90 = Ti.UI.createAnimation({
		    transform: rotateRight90,
		    duration: 1000,
		});
		
		higlightedBlock.animate(animationBlockRight90);
		
		changeCurrentPosition(higlightedBlock, higlightedBlock.currentRotation);
		
		if(higlightedBlock.currentRotation == startRotation){
			rotationIsNew = false;
		}else{
			rotationIsNew = true;
		}
		
		checkForWinner();
	}
}

function changePlayer(currentPlayer){
	if(currentPlayer == 1){
		currentPlayer = 2;
		viewCurrentPlayer1.backgroundImage = 'images/player1NotActive.png';
		viewCurrentPlayer2.backgroundImage = 'images/player2Active.png';
		moveCount++;
		if(moveCount == 36){
			player1Won = true;
			player2Won = true;
			gameOver('Draw', 'Nice game! You are equally awesome!');
		}
	}else{
		currentPlayer = 1;
		viewCurrentPlayer1.backgroundImage = 'images/player1Active.png';
		viewCurrentPlayer2.backgroundImage = 'images/player2NotActive.png';
		moveCount++;
		if(moveCount == 36){
			player1Won = true;
			player2Won = true;
			gameOver('Draw', 'Nice game! You are equally awesome!');
		}
	}
	enableToChange = false;
	return currentPlayer;
}

function changeAction(currentAction){
	if(currentAction == 0){
		currentAction = 1;
		viewCurrentAction.backgroundImage = 'images/action1.png';
	}else{
		currentAction = 0;
		viewCurrentAction.backgroundImage = 'images/action0.png';
		
		for(i = 0 ; i < 4 ; i++){
			blocks[i].backgroundImage = 'images/block.png';
		}
		higlightedBlock = '';
		
		currentPlayer = changePlayer(currentPlayer);
		
	}
	return currentAction;
}

function changeCurrentPosition(higlightedBlock, currentRotation){
	
	var cp = 0;
	
	if(currentRotation == 360 || currentRotation == -360 || currentRotation == 0){
		for(i = 0 ; i < 9 ; i++){
			currPos[higlightedBlock.index][i] = blocks[higlightedBlock.index][i];
		}
	}
	
	else if(currentRotation == -90 || currentRotation == 270){
		var cp = 6;
		for(i = 0 ; i < 9 ; i++){
			if(cp == -3){
				cp = 7;	
			}
			if(cp == -2){
				cp = 8;	
			}
			currPos[higlightedBlock.index][cp] = blocks[higlightedBlock.index][i];
			cp = cp - 3;
		}
	}
	
	else if(currentRotation == -180 || currentRotation == 180){
		cp = 8;
		for(i = 0 ; i < 9 ; i++){
			currPos[higlightedBlock.index][cp] = blocks[higlightedBlock.index][i];
			cp--;
		}
	}
	
	else if(currentRotation == 90 || currentRotation == -270){
		cp = 2;
		for(i = 0 ; i < 9 ; i++){
			if(cp == 11){
				cp = 1;	
			}
			if(cp == 10){
				cp = 0;	
			}
			blocks[higlightedBlock.index][i].currentPosition = cp;
			currPos[higlightedBlock.index][cp] = blocks[higlightedBlock.index][i];
			cp = cp + 3;
		}
	}
	
}

function checkForWinner(){
	
	testWinner(0, 0, 0, 1, 0, 2, 1, 0, 1, 1);
	testWinner(0, 3, 0, 4, 0, 5, 1, 3, 1, 4);
	testWinner(0, 6, 0, 7, 0, 8, 1, 6, 1, 7);
	testWinner(2, 0, 2, 1, 2, 2, 3, 0, 3, 1);
	testWinner(2, 3, 2, 4, 2, 5, 3, 3, 3, 4);
	testWinner(2, 6, 2, 7, 2, 8, 3, 6, 3, 7);
	
	testWinner(0, 1, 0, 2, 1, 0, 1, 1, 1, 2);
	testWinner(0, 4, 0, 5, 1, 3, 1, 4, 1, 5);
	testWinner(0, 7, 0, 8, 1, 6, 1, 7, 1, 8);
	testWinner(2, 1, 2, 2, 3, 0, 3, 1, 3, 2);
	testWinner(2, 4, 2, 5, 3, 3, 3, 4, 3, 5);
	testWinner(2, 7, 2, 8, 3, 6, 3, 7, 3, 8);
	
	testWinner(0, 0, 0, 3, 0, 6, 2, 0, 2, 3);
	testWinner(0, 1, 0, 4, 0, 7, 2, 1, 2, 4);
	testWinner(0, 2, 0, 5, 0, 8, 2, 2, 2, 5);
	testWinner(1, 0, 1, 3, 1, 6, 3, 0, 3, 3);
	testWinner(1, 1, 1, 4, 1, 7, 3, 1, 3, 4);
	testWinner(1, 2, 1, 5, 1, 8, 3, 2, 3, 5);
	
	testWinner(0, 3, 0, 6, 2, 0, 2, 3, 2, 6);
	testWinner(0, 4, 0, 7, 2, 1, 2, 4, 2, 7);
	testWinner(0, 5, 0, 8, 2, 2, 2, 5, 2, 8);	
	testWinner(1, 3, 1, 6, 3, 0, 3, 3, 3, 6);
	testWinner(1, 4, 1, 7, 3, 1, 3, 4, 3, 7);
	testWinner(1, 5, 1, 8, 3, 2, 3, 5, 3, 8);
	
	testWinner(1, 5, 1, 7, 3, 0, 2, 5, 2, 7);
	testWinner(1, 2, 1, 4, 1, 6, 2, 2, 2, 4);
	testWinner(1, 4, 1, 6, 2, 2, 2, 4, 2, 6);
	testWinner(1, 1, 1, 3, 0, 8, 2, 1, 2, 3);
	
	testWinner(0, 3, 0, 7, 2, 2, 3, 3, 3, 7);
	testWinner(0, 0, 0, 4, 0, 8, 3, 0, 3, 4);
	testWinner(0, 4, 0, 8, 3, 0, 3, 4, 3, 8);
	testWinner(0, 1, 0, 5, 1, 6, 3, 1, 3, 5);
	
	if(player1Won && player2Won){
		soundWinner();
		gameOver('Draw', 'Nice game! You are equally awesome!');
		return;
	}
	
	if(player1Won){
		soundWinner();
		gameOver('Winner', 'Player 1 is the winner!');
		return;
	}
	
	if(player2Won){
		soundWinner();
		gameOver('Winner', 'Player 2 player is the winner!');
		return;
	}
	
}

function gameOver(winnerTitle, winnerText){
	var dialog = Ti.UI.createAlertDialog({
			title: winnerTitle,
			message: winnerText,
			buttonNames: ['Quit', 'Play Again']
	});
	dialog.addEventListener('click', function(e){
		if(e.index == 0){
			soundMarblePlaced();
			win.close();
		}else{
			// Reset everything to deafult values...
			soundMarblePlaced();
			for(i = 0 ; i < 4 ; i++){
				blocks[i].backgroundImage = 'images/block.png';
					for(x = 0 ; x < 9 ; x++){
						blocks[i][x].backgroundImage = 'images/hole.png';
					}
			}
			initCurrPos();
			currentAction = 0;
			currentPlayer = 1;
			higlightedBlock = '';
			lastChoosen = '';
			enableToChange = false;
			startLocation;
			
			player1Won = false;
			player2Won = false;
			
			startRotation = 0;
			rotationIsNew = false;
			
			moveCount = 0;
		}
	});
	dialog.show();
	return;
}


function testWinner(a0, a1, b0, b1, c0, c1, d0, d1, e0, e1){

	if(	currPos[a0][a1].backgroundImage == currPos[b0][b1].backgroundImage && 
		currPos[b0][b1].backgroundImage == currPos[c0][c1].backgroundImage && 
		currPos[c0][c1].backgroundImage == currPos[d0][d1].backgroundImage &&
		currPos[d0][d1].backgroundImage == currPos[e0][e1].backgroundImage &&
		currPos[a0][a1].backgroundImage != "images/hole.png" && currPos[a0][a1].backgroundImage != undefined)
	{
		if(currPos[a0][a1].backgroundImage == player1Color){
			player1Won = true;
		}else{
			player2Won = true;
		}	
	}
}

function initCurrPos(){
	for(i = 0 ; i < 4 ; i++){
		changeCurrentPosition(blocks[i], blocks[i].currentRotation);
	}
}



//
//
// ********** USER INTERFACE **********

var viewNavigation = Ti.UI.createView({
	height: '40dp',
	top: '0dp',
	backgroundImage:'images4/navBar.png'
});
win.add(viewNavigation);

var btnBack = Ti.UI.createButton({
	title: 'Quit',
	left: '10dp',
	backgroundImage: 'images4/navBarButton.png',
	width:'50dp',
	height:'30dp'
});

btnBack.addEventListener('click', function(e){
	
	soundMarblePlaced();
	
	var closeDialog = Ti.UI.createAlertDialog({
    	cancel: 1,
    	buttonNames: ['Quit', 'Cancel'],
    	message: 'Are you sure you want to quit the game?',
    	title: 'Quit'
  	});
  	
	closeDialog.addEventListener('click', function(e){
	   	if (e.index === e.source.cancel){
	    	soundMarblePlaced();
	   	}else{
	   		soundMarblePlaced();
	   		win.close();
	   	}
	});
	closeDialog.show();	
});

viewNavigation.add(btnBack);

var btnHelp = Ti.UI.createButton({
	title: 'Help',
	right: '10dp',
	backgroundImage: 'images4/navBarButton.png',
	width:'50dp',
	height:'30dp'
});

btnHelp.addEventListener('click', function(e){
	soundMarblePlaced();
	
	var aboutWin = Ti.UI.createWindow({
		url: 'rules.js',
		backgroundImage: 'images/backgroundLarge.png'	
	});
	aboutWin.open({
  		modal:true,
  		modalTransitionStyle: Ti.UI.iPhone.MODAL_TRANSITION_STYLE_FLIP_HORIZONTAL,
	});		
});

viewNavigation.add(btnHelp);

var viewInformation = Ti.UI.createView({
	height: '55dp',
	top: '40dp',
	backgroundColor:'transparent'
});
win.add(viewInformation);

var viewCurrentAction = Ti.UI.createView({
	backgroundImage: 'images/action0.png',
	width:'155dp',
	height:'55dp',
	top: '-1dp'
});
viewInformation.add(viewCurrentAction);

var viewCurrentPlayer1 = Ti.UI.createView({
	backgroundImage: 'images/player1Active.png',
	width:'80dp',
	height:'29dp',
	left:'0dp',
	top:'-2dp'
});
viewInformation.add(viewCurrentPlayer1);

var viewPlayer1Color = Ti.UI.createView({
	backgroundImage: miniColor1,
	width:'20dp',
	height:'20dp',
	left:'30dp',
	top:'26dp'
});
viewInformation.add(viewPlayer1Color);

var viewCurrentPlayer2 = Ti.UI.createView({
	backgroundImage: 'images/player2NotActive.png',
	width:'80dp',
	height:'29dp',
	right:'0dp',
	top:'-2dp'
});
viewInformation.add(viewCurrentPlayer2);

var viewPlayer2Color = Ti.UI.createView({
	backgroundImage: miniColor2,
	width:'20dp',
	height:'20dp',
	right:'30dp',
	top:'26dp'
});
viewInformation.add(viewPlayer2Color);

var viewGame = Ti.UI.createView({
	height: '320dp',
	width:'320dp',
	top: '92dp',
	layout: 'horizontal'
});

win.add(viewGame);

// Skapar alla block och hål...
for(i = 0 ; i < 4 ; i++){
	blocks[i] = Ti.UI.createView({
		width:'160dp',
		height:'160dp',
		backgroundImage: 'images/block.png',
		layout: 'horizontal',
		anchorPoint : {
        	x : 0.5,
        	y : 0.5
    	},
    	currentRotation: 0,
    	index: i
	});
	currPos[i] = Ti.UI.createView();
	
	blocks[i].addEventListener('click', function(e){
		if(currentAction == 1){
			chooseBlock(e.source.index);
		}
	});
	
	for(x = 0 ; x < 9 ; x++){
		blocks[i][x] = Ti.UI.createView({
			width:'40dp',
			height:'40dp',
			left: '10dp',
			top: '10dp',
			backgroundImage: 'images/hole.png',
			blockNumber: i,
			holeNumber: x,
    		bubbleParent: false
		}); 
		currPos[i][x] = Ti.UI.createView();
		
		blocks[i][x].addEventListener('click', function(e){
			if(currentAction == 0){
				chooseHole(e.source.blockNumber, e.source.holeNumber);
			}else{
				chooseBlock(e.source.blockNumber);
			}
		});
		
		blocks[i].add(blocks[i][x]);
		currPos[i].add(currPos[i][x]);
	}
	
	viewGame.add(blocks[i]); 
}

var btnRotateRight = Ti.UI.createButton({
	backgroundImage: 'images4/rightArrow.png',
	right: '10dp',
	top: '422dp',
	width:'50dp',
	height:'30dp',
	rightOrLeft: 'right'
});

btnRotateRight.addEventListener('click', function(e){
	if(currentAction != 1){
		soundError();
		Ti.UI.createAlertDialog({title: 'Ooops!', message: 'Place a marker on the table and press confirm before you rotate' }).show();
		return;
	}else if(higlightedBlock == ''){
		soundError();
		Ti.UI.createAlertDialog({title: 'Ooops!', message: 'Highlight the block you would like to rotate' }).show();
		return;
	}
	rotateBlock(e.source.rightOrLeft);	
});

win.add(btnRotateRight);

var btnRotateLeft = Ti.UI.createButton({
	backgroundImage: 'images4/leftArrow.png',
	left: '10dp',
	top: '422dp',
	width:'50dp',
	height:'30dp',
	rightOrLeft: 'left'
});

btnRotateLeft.addEventListener('click', function(e){
	if(currentAction != 1){
		soundError();
		Ti.UI.createAlertDialog({title: 'Ooops!', message: 'Place a marker on the table and press confirm before you rotate' }).show();
		return;
	}else if(higlightedBlock == ''){
		soundError();
		Ti.UI.createAlertDialog({title: 'Ooops!', message: 'Highlight the block you would like to rotate' }).show();
		return;
	}
	rotateBlock(e.source.rightOrLeft);
});

win.add(btnRotateLeft);

var btnDone = Ti.UI.createButton({
	title: 'Confirm',
	backgroundImage: 'images4/playButton.png',
	top: '422dp',
	width:'90',
	height:'30',
});

btnDone.addEventListener('click', function(e){
	
	if(currentAction == 0){
		if(lastChoosen != ''){
			soundMarblePlaced();
			currentAction = changeAction(currentAction);
			lastChoosen = '';
			return;
		}else{
			soundError();
			Ti.UI.createAlertDialog({title: 'Ooops!', message: 'Place a marker on the table and press confirm before you rotate' }).show();
			return;	
		}
	}else{
		if(rotationIsNew){
			soundMarblePlaced();
			currentAction = changeAction(currentAction);
		}else{
			soundError();
			Ti.UI.createAlertDialog({title: 'Ooops!', message: 'You have to rotate one of the blocks before you confirm.' }).show();
		}
	}
	
});
win.add(btnDone);

initCurrPos();

