import { Injectable } from '@angular/core';
import {Http, RequestOptions, BaseRequestOptions, Headers}  from '@angular/http';
import { Config } from './config.service';

import { DataSet } from '../models/profile';

@Injectable()
export class DatasetsService {

  constructor(private http: Http, private config: Config) {}
  getData():Element[] {
    return ELEMENT_DATA;
  }
  /**
   * Return DataSets
   */
  getDataSets(domain:string): Promise<DataSet[]> {
    console.log("come in getDataSets",domain);
    return this.http.get(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/datasets`)
    .map(response => response.json() as DataSet[])
    .toPromise();
  }
}
//demo data
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