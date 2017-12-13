import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {DatasetsService} from '../../services/datasets.service';

import { DataSet, Configuration } from '../../models/profile';

@Component({
  selector: 'app-datasets',
  templateUrl: './datasets.component.html',
  styleUrls: ['./datasets.component.css']
})
export class DatasetsComponent implements OnInit {
  datasets: DataSet[];
  configuration: Configuration;
  displayedColumns: any;
  dataSource: any;

  constructor(private datasetService: DatasetsService,public dialog: MatDialog) { }

  ngOnInit() {
    this.datasetService.getDataSets('test1').then(ds => {
      this.datasets = ds;
      this.displayedColumns = ['id', 'configuration','modification'];
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
  

  openDialog4CreateDS() {
    const dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
      height: '30%',
      width: '40%',
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed',result);
      //this.animal = result;
    });
    /*
    const dialogRef =this.dialog.open(CreateDataSetsDialog, {
      width:'350px'
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });*/
    console.log("come openDialog4CreateDS();");
  }
}


@Component({
  selector : 'create-dataset-dialog',
  templateUrl : 'create-dataset-dialog.html',
  styleUrls: ['./datasets.component.css']
})
export class CreateDatasetDialogComponent {
  //constructor() {}
  constructor(public dialogRef: MatDialogRef<CreateDatasetDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { 

  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
