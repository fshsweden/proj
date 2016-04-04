grammar Indicators;

@header {
	package com.ev112.antlr;
}

prog			: constants* 
				  indicators*
				  rules* 
				  ;

/*
 * Constants -- example
 * 
 * CONSTANTS {
 *        Double MIN_VALUE = 123.456;
 * 		  Boolean USE_SIGNALS = TRUE;
 * }
 * 
 */

constants		: 'CONSTANTS' 	'{' constantDef* '}' ;
                  
indicators		: 'INDICATORS' 	'{' indicatorDef* '}' ;

rules			: 'RULES' 		'{' ruleDef* '}';
                  
dataType		: numberType | boolType ;

constantDef		: numberDef ';' | boolDef ';' ;

numberDef		: numberType ID '=' NUMBER ;

numberType		: 'Integer' | 'Double' | 'Long' ;

boolDef			: boolType ID '=' boolValue ;

boolType		: 'Boolean' ;

boolValue		: 'TRUE' | 'FALSE' ;

indicatorDef	: dataType variableId '=' indicatorFunc ';' ;

indicatorFunc	: indicatorName paramDef ; 

variableId		: ID ;

indicatorName	: ID ;

paramDef 		: '(' ')'
				| '(' param (',' param)* ')' ;
				
param			: idParam | numberParam | indicatorFunc ;

ruleDef			: upDef | downDef | exitUpDef | exitDownDef;

upDef			: 'UP' ID ';' ;

downDef			: 'DOWN' ID ';' ;

exitUpDef		: 'UP_EXIT' ID ';' ;

exitDownDef		: 'DOWN_EXIT' ID ';' ;

idParam			: ID;

numberParam		: NUMBER;

ID				: [a-zA-Z_]+ ;

NUMBER			: [0-9.-]+ ;

LINE_COMMENT 	: '//' .*? '\n' -> skip ;

COMMENT 		: '/*' .*? '*/' -> skip ;

WS 				: [ \t\n\r]+ -> skip ;

LINE_NUMBER		: '[' [0-9]+ ']'-> skip ;

