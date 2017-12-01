import { Component } from '@angular/core';
import {MatSidenavModule, MatTableDataSource, MatSort} from '@angular/material';
import { OnInit } from '@angular/core/src/metadata/lifecycle_hooks';
import { LoginService } from './services/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';

  constructor(private login: LoginService) {}

  ngOnInit() { }

}
