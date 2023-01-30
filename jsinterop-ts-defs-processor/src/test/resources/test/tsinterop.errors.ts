import {Client} from "../tsinterop";

let person= new Client();

// THROWS Cannot assign to 'lastName' because it is a read-only property
person.lastName = 'abc';
// THROWS Cannot assign to 'ACONSTANT' because it is a read-only property
Client.ACONSTANT = 'abc';
// THROWS Type 'number' is not assignable to type 'string'
person.initialName=1;
// THROWS Type 'string' is not assignable to type 'number'
person.floatF ='abc';
