import { Injectable } from '@angular/core';


@Injectable()
export class DatasetsService {

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
  {position: 1, name: 'Dataset Hydrogen', weight: 1.0079, symbol: 'H'},
  {position: 2, name: 'Dataset Helium', weight: 4.0026, symbol: 'He'},
  {position: 3, name: 'Dataset Lithium', weight: 6.941, symbol: 'Li'}
];