import { Injectable } from '@angular/core';

@Injectable()
export class UsersService {

  constructor() { }
  getData():Element[] {
    return ELEMENT_DATA;
  }
}
export interface Element {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

const ELEMENT_DATA: Element[] = [
  {position: 5, name: 'User Boron', weight: 10.811, symbol: 'B'},
  {position: 6, name: 'User Carbon', weight: 12.0107, symbol: 'C'},
  {position: 7, name: 'User Nitrogen', weight: 14.0067, symbol: 'N'}
];
