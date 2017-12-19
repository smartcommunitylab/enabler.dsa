import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { HttpModule } from '@angular/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {MatInputModule} from '@angular/material';

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { CustomMaterialModule } from './custom-material.modul';

import { AppComponent } from './app.component';
import { AuthGuard, AppRoutingModule } from './app-routing.module';

import { LoginComponent }      from './components/login/login.component';
import { HeaderComponent, ErrorDialogComponent } from './components/header/header.component';
import { DatasetsComponent, CreateDatasetDialogComponent } from './components/datasets/datasets.component';
import { ManagersComponent, CreateManagerDialogComponent } from './components/managers/managers.component';
import { UsersComponent, CreateUserDialogComponent } from './components/users/users.component';

import { Config } from './services/config.service';
import { LoginService } from './services/login.service';
import { DataService, requestOptionsProvider } from './services/data.service';
import { DatasetsService } from './services/datasets.service';
import { ManagersService } from './services/managers.service';
import { UsersService } from './services/users.service';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HeaderComponent,
    ErrorDialogComponent,
    DatasetsComponent,
    ManagersComponent,
    UsersComponent,
    CreateDatasetDialogComponent,
    CreateManagerDialogComponent,
    CreateUserDialogComponent
  ],
  entryComponents: [
    ErrorDialogComponent,
    CreateDatasetDialogComponent,
    CreateManagerDialogComponent,
    CreateUserDialogComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CustomMaterialModule,
    HttpModule,
    FormsModule,
    MatInputModule
  ],
  providers: [requestOptionsProvider,
    Config,
    DataService,
    LoginService,
    AuthGuard,
    DatasetsService,
    ManagersService,
    UsersService],
  bootstrap: [AppComponent]
})
export class AppModule { }