import { Component, OnInit, ViewChild } from '@angular/core';
import {MatTableDataSource, MatSort} from '@angular/material';
import {UsersService} from '../../services/users.service';
import { User } from '../../models/profile';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  users:User[];
  displayedColumns:any;
  dataSource:any;
  constructor(private userService: UsersService) { }
  
  ngOnInit() {
    this.userService.getUsers('test1').then(user => {
      this.users = user;
      this.displayedColumns = ['id', 'email', 'dataset', 'modification'];
      this.dataSource = new MatTableDataSource<User>(user);
    });
  }

  @ViewChild(MatSort) sort: MatSort;
  
  /**
   * Set the sort after the view init since this component will
   * be able to query its view for the initialized sort.
   */
  
  ngAfterViewInit() {
    //this.dataSource.sort = this.sort;
  }

}
