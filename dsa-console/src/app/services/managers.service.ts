import { Injectable } from '@angular/core';
import {Http, RequestOptions, BaseRequestOptions, Headers}  from '@angular/http';
import { Config } from './config.service';

import { Manager } from '../models/profile';

@Injectable()
export class ManagersService {

  constructor(private http: Http, private config: Config) {}
  getData():Element[] {
    return ELEMENT_DATA;
  }

  /**
   * Return DataSets
   */
  getDataSets(domain:string): Promise<Manager[]> {
    console.log("come in getDataSets",domain);
    return this.http.get(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/managers`)
    .map(response => response.json() as Manager[])
    .toPromise();
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
