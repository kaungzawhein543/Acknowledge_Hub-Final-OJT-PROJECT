import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service'; // Adjust import path as needed

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const requiredRoles = next.data['roles'] as string[] || [];
    const requiredPositions = next.data['positions'] as string[] || [];
    const excludedRoles = next.data['excludedRoles'] as string[] || [];
    const excludedPositions = next.data['excludedPositions'] as string[] || [];
    const currentRoute = state.url;

    return this.authService.getUserInfo().pipe(
      switchMap(userInfo => {
        let hasRole = requiredRoles.length > 0 ? requiredRoles.includes(userInfo.user.role) : true;
        let hasPosition = requiredPositions.length > 0 ? requiredPositions.includes(userInfo.position) : true;

        let isRoleExcluded = excludedRoles.includes(userInfo.user.role);
        let isPositionExcluded = excludedPositions.includes(userInfo.position);

        // Determine access based on position and route
        if (currentRoute.includes('/acknowledgeHub/staff-dashboard')) {
          // Staff-dashboard: Accessible to any role/position except ADMIN and HR_MAIN
          hasPosition = userInfo.user.role !== 'ADMIN' && userInfo.position !== 'HR_MAIN';
        } else if (currentRoute.includes('/acknowledgeHub/system-dashboard')) {
          // HR-dashboard: Accessible to HR_MAIN and ADMIN roles
          hasPosition = userInfo.position === 'HR_MAIN' || userInfo.user.role === 'ADMIN';
        // } else if (currentRoute.includes('/admin-dashboard')) {
        //   // Admin-dashboard: Only accessible to ADMIN
        //   hasPosition = userInfo.position === 'ADMIN';
        } else if (currentRoute.includes('/acknowledgeHub/company')) {
          // Company route logic
          if (userInfo.user.role === 'ADMIN') {
            // ADMIN can access regardless of position
            hasPosition = true;
          } else if (userInfo.user.role === 'USER') {
            // USER can only access if they have the HR_MAIN position
            hasPosition = userInfo.position === 'HR_MAIN';
          }
        } else if (currentRoute.includes('/acknowledgeHub/department')) {
          // Department route logic
          if (userInfo.user.role === 'ADMIN') {
            // ADMIN can access regardless of position
            hasPosition = true;
          } else if (userInfo.user.role === 'USER') {
            // USER can only access if they have the HR_MAIN position
            hasPosition = userInfo.position === 'HR_MAIN';
          }
        } else if (requiredPositions.length > 0) {
          // Check if ADMIN role is present in required roles
          if (requiredRoles.includes('ADMIN') && userInfo.user.role === 'ADMIN') {
            hasPosition = true; // ADMIN can access regardless of position
          } else {
            hasPosition = requiredPositions.includes(userInfo.position);
          }
        }

        // Exclude logic: If the role or position is excluded, deny access
        if (isRoleExcluded || isPositionExcluded) {
          return of(false);
        }

        return of(hasRole && hasPosition);
      }),
      map(hasAccess => {
        if (!hasAccess) {
          console.log('Access Denied');
          this.router.navigate(['/acknowledgeHub/404']); // Redirect to a 404 page or any other appropriate route
          return false;
        }
        return true;
      }),
      catchError(error => {
        console.error('Role guard error:', error);
        this.router.navigate(['/acknowledgeHub/announcement']); // Adjust the redirect URL as needed
        return of(false);
      })
    );
  }
}