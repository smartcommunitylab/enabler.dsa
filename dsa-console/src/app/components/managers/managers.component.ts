import { Component, OnInit, ViewChild } from '@angular/core';
import {MatTableDataSource, MatSort} from '@angular/material';
import {ManagersService} from '../../services/managers.service';
import { Manager } from '../../models/profile';

@Component({
  selector: 'app-managers',
  templateUrl: './managers.component.html',
  styleUrls: ['./managers.component.css']
})
export class ManagersComponent implements OnInit {
  managers:Manager[];
  displayedColumns:any;
  dataSource:any;
  constructor(private managerService: ManagersService) { }
  
  ngOnInit() {
    this.managerService.getDataSets('test1').then(mng => {
      this.managers = mng;
      this.displayedColumns = ['id', 'email','modification'];
      this.dataSource = new MatTableDataSource<Manager>(mng);
    });
  }
  
  //displayedColumns = ['position', 'name', 'weight', 'symbol'];
  //dataSource = new MatTableDataSource(this.datasetService.getData());

  @ViewChild(MatSort) sort: MatSort;
  
  /**
   * Set the sort after the view init since this component will
   * be able to query its view for the initialized sort.
   */
  
  ngAfterViewInit() {
    //this.dataSource.sort = this.sort;
  }

}
