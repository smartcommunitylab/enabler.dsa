import { Component, OnInit, ViewEncapsulation } from '@angular/core';

import {MatDialog} from '@angular/material';

import { LoginService } from '../../services/login.service';
import { DataService } from '../../services/data.service';

import { UserProfile, DomainProfile} from '../../models/profile';
import {ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  profile: UserProfile;
  currentDomain: DomainProfile;
  domains: string[];

  constructor(private login: LoginService, private data: DataService, private dialog: MatDialog, private route: ActivatedRoute, private router: Router) {
  }

  private processDomainChanged(paramDomain: string) {
    if (!paramDomain) {
      this.router.navigate(['/', this.profile.domains[0].domain]);
    }
    this.profile.domains.forEach(d => {
      if (d.domain === paramDomain) {
        this.currentDomain = d;
      }
    });
    if (this.currentDomain == null) {
      this.currentDomain = this.profile.domains[0];
    }
  }

  ngOnInit() {
    this.data.getUserProfile().then(profile => {
      profile.domains = profile.domains.filter(d => d.role === 'DOMAIN_OWNER' || d.role === 'DOMAIN_MANAGER');
      if (!profile.domains) {
        this.showError();
      } else {
        this.profile = profile;
        this.domains = profile.domains.map(d => d.domain);
        this.route.params.subscribe(params => {
          this.processDomainChanged(params['domain']);
        });
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
  changeDomain(event: any) {
    console.log('change selected', event.value);
    // this.currentDomain = event.value.domain;
    // sessionStorage.setItem('currentDomain',event.value.domain);
  }

}

@Component({
  selector: 'app-error-dialog',
  templateUrl: 'app-error-dialog.html',
})
export class ErrorDialogComponent {}


