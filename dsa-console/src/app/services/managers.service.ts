import { Injectable } from '@angular/core';
import {Http, RequestOptions, BaseRequestOptions, Headers}  from '@angular/http';
import {HttpErrorResponse} from '@angular/common/http';
import { Config } from './config.service';

import { Manager, BodyDataManager } from '../models/profile';

@Injectable()
export class ManagersService {

  constructor(private http: Http, private config: Config) {}

  /**
   * Return ManagerList
   */
  getManagers(domain:string): Promise<Manager[]> {
    //console.log("come in getDataSets",domain);
    return this.http.get(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/managers`)
    .map(response => response.json() as Manager[])
    .toPromise();
  }

  /**
   * Set Manager/ create a new manager
   */
  setManager(domain:string, body:BodyDataManager): any{
    return this.http.post(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/managers/`,body).subscribe(
      data => {
        console.log("Return Data from post(create): " + data);
      },
      (err: HttpErrorResponse) => {
        if (err.error instanceof Error) {
          console.log("Client-side error occured.");
        } else {
          console.log("Server-side error occured.");
        }
      }
    );
  }

  /**
   * Edit Manager
   */
  editManager(domain:string, dsID:string, body:BodyDataManager){
    return this.http.put(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/managers/${dsID}`,body).subscribe(
      data => {
        console.log("Return Data from put(edit): " + data);
      },
      (err: HttpErrorResponse) => {
        if (err.error instanceof Error) {
          console.log("Client-side error occured.");
        } else {
          console.log("Server-side error occured.");
        }
      }
    );
  }

  /**
   * Delete Manager
   */
  deleteManager(domain: string, mngID: string){
    return this.http.delete(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/managers/${mngID}`).subscribe(
      data => {
        console.log("Return Data from delete(delete): " + data);
      },
      (err: HttpErrorResponse) => {
        if (err.error instanceof Error) {
          console.log("Client-side error occured.");
        } else {
          console.log("Server-side error occured.");
        }
      }
    );
  }
}

