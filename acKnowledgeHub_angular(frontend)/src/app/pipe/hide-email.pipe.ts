import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'hideEmail'
})
export class HideEmailPipe implements PipeTransform {
  transform(value: string): string {
    const [user, domain] = value.split('@');
    const userLength = user.length;

    if (userLength <= 3) {
      return value;
    }

    const start = user.substring(0, 2);
    const end = user.substring(userLength - 1, userLength);

    const maskedUser = start + '*'.repeat(userLength - 3) + end;

    return maskedUser + '@' + domain;
  }
}
