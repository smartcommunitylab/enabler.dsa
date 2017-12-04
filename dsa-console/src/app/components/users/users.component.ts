import { Component, OnInit, ViewChild } from '@angular/core';
import {MatTableDataSource, MatSort} from '@angular/material';
import {UsersService} from '../../services/users.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  constructor(private datasetService: UsersService) { }
  
    ngOnInit() {
    }
    
    displayedColumns = ['position', 'name', 'weight', 'symbol'];
    dataSource = new MatTableDataSource(this.datasetService.getData());
  
    @ViewChild(MatSort) sort: MatSort;
    
    /**
     * Set the sort after the view init since this component will
     * be able to query its view for the initialized sort.
     */
    
    ngAfterViewInit() {
      this.dataSource.sort = this.sort;
    }

}
