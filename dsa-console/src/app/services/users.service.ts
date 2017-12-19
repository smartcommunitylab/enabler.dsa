import { Injectable } from '@angular/core';
import {Http, RequestOptions, BaseRequestOptions, Headers}  from '@angular/http';
import { Config } from './config.service';
import {HttpErrorResponse} from '@angular/common/http';
import { User, BodyDataUser } from '../models/profile';

@Injectable()
export class UsersService {

  constructor(private http: Http, private config: Config) {}
  
  /**
   * Return ManagerList
   */
  getUsers(domain:string): Promise<User[]> {
    //console.log("come in getDataSets",domain);
    return this.http.get(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/users`)
    .map(response => response.json() as User[])
    .toPromise();
  }

  /**
   * Set DataSets/ create a new dataset
   */
  setUser(domain:string, body:BodyDataUser): any{
    return this.http.post(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/users/`,body).subscribe(
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
   * Edit User
   */
  editUser(domain:string, userID:string, body:BodyDataUser){
    return this.http.put(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/users/${userID}`,body).subscribe(
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
   * Delete User
   */
  deleteUser(domain: string, userID: string){
    return this.http.delete(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/users/${userID}`).subscribe(
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
