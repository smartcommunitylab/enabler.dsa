import { Injectable } from '@angular/core';

@Injectable()
export class ManagersService {

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
  {position: 5, name: 'Manager Boron', weight: 10.811, symbol: 'B'},
  {position: 6, name: 'Manager Carbon', weight: 12.0107, symbol: 'C'},
  {position: 7, name: 'Manager Nitrogen', weight: 14.0067, symbol: 'N'}
];
