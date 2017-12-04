import { Component, OnInit, ViewEncapsulation } from '@angular/core';

import {MatDialog} from '@angular/material';

import { LoginService } from '../../services/login.service';
import { DataService } from '../../services/data.service';

import { UserProfile, DomainProfile} from '../../models/profile';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  profile: UserProfile;
  currentDomain: DomainProfile;
  domains: string[];

  constructor(private login: LoginService, private data: DataService, private dialog: MatDialog) {}

  ngOnInit() {
    this.data.getUserProfile().then(profile => {
      profile.domains = profile.domains.filter(d => d.role === 'DOMAIN_OWNER' || d.role === 'DOMAIN_MANAGER');
      if (!profile.domains) {
        this.showError();
      } else {
        this.profile = profile;
        this.currentDomain = profile.domains[0];
        this.domains = profile.domains.map(d => d.domain);
      }
    }, err => {
      console.log('Error reading profile', err);
      this.showError(err);
    });
  }

  private showError(err?: any) {
    const dialogRef = this.dialog.open(ErrorDialogComponent);
  }

  logout() {
    this.login.logout();
  }

}

@Component({
  selector: 'app-error-dialog',
  templateUrl: 'app-error-dialog.html',
})
export class ErrorDialogComponent {}


