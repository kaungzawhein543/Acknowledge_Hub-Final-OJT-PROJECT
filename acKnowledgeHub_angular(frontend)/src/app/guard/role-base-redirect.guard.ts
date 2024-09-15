import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';
import { Observable,catchError, of, switchMap } from 'rxjs';
import {  map,  } from 'rxjs/operators';

export const roleBaseRedirectGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService); // Injecting the AuthService
  const router = inject(Router); // Injecting the Router
  

  return authService.getUserInfo().pipe(
    switchMap(userInfo => {

      if (!userInfo) {
        // If no user is logged in, redirect to the login page
        router.navigate(['/login']);
        return of(false);
      }
      if (userInfo.user.role === 'ADMIN' || userInfo.position === 'HR_MAIN') {
        router.navigate(['/dashboard']);
        return of(false);
      } else if (userInfo.user.role === 'USER') {
        router.navigate(['/staff-dashboard']);
        return of(false);
      }
      // If no conditions match, return false and redirect to 404
      router.navigate(['/404']);
      return of(false);
    }),
    catchError(error => {
      console.error('Role-based redirect guard error:', error);
      router.navigate(['/404']); // Redirect to 404 page or another appropriate route
      return of(false);
    })
  );
};
