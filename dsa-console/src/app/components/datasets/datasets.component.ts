import { Component, OnInit, ViewChild } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MAT_DIALOG_DATA} from '@angular/material';
import {DatasetsService} from '../../services/datasets.service';

import { DataSet, Configuration } from '../../models/profile';

@Component({
  selector: 'app-datasets',
  templateUrl: './datasets.component.html',
  styleUrls: ['./datasets.component.css']
})
export class DatasetsComponent implements OnInit {
  datasets: DataSet[];
  configuration:Configuration;
  displayedColumns:any;
  dataSource:any;

  constructor(private datasetService: DatasetsService,public dialog: MatDialog) { }

  ngOnInit() {
    this.datasetService.getDataSets('test1').then(ds => {
      this.datasets = ds;
      console.log("getDataSets is:",ds);
      //setTimeout(() => {      },5000);
      this.displayedColumns = ['id', 'configuration','modification'];
      //this.dataSource = new MatTableDataSource([{id: "ds1", configuration: {}}, {id: "ds2", configuration: {}}, {id: "ds3", configuration: {}}]);
      this.dataSource = new MatTableDataSource<DataSet>(ds);
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
  

  openDialog4CreateDS(){
    this.dialog.open(CreateDataSetsDialog, {
      data: {
        animal: 'panda'
      }
    });
    console.log("come openDialog4CreateDS();");
  }
}


@Component({
  selector : 'delete-all-component',
  template : `<h2 mat-dialog-title>Delete all</h2>
  <mat-dialog-content>Are you sure?</mat-dialog-content>
  <mat-dialog-actions>
    <button mat-button mat-dialog-close>No</button>
    <button mat-button [mat-dialog-close]="true">Yes</button>
  </mat-dialog-actions>`,
})
export class CreateDataSetsDialog {
  constructor() {}
}
