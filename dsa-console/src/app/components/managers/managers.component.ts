import { Component, OnInit, ViewChild } from '@angular/core';
import {MatTableDataSource, MatSort} from '@angular/material';
import {ManagersService} from '../../services/managers.service';

@Component({
  selector: 'app-managers',
  templateUrl: './managers.component.html',
  styleUrls: ['./managers.component.css']
})
export class ManagersComponent implements OnInit {

  constructor(private datasetService: ManagersService) { }
  
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
