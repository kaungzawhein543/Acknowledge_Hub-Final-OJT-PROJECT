import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastContainer: HTMLElement;

  constructor() {
    this.toastContainer = document.getElementById('toast-container')!;
  }

  showToast(message: string, type: 'success' | 'error' | 'info' = 'info') {
    const toast = document.createElement('div');
    toast.className = `flex items-center w-auto p-2 text-gray-500 rounded-lg border border-gray-200 shadow-md ${this.getToastClass(type)}`;
    toast.setAttribute('role', 'alert');

    toast.innerHTML = `
    <div class="inline-flex items-center justify-center bg-white shadow-md  p-1 flex-shrink-0 w-8 h-8 ${this.getIconClass(type)} rounded-lg">
      ${this.getIcon(type)}
      <span class="sr-only">${type} icon</span>
    </div>
    <div class="ms-3 text-sm font-normal w-full ${this.getTextClass(type)}">${message}</div>
    <button type="button" class="ms-auto -mx-1.5 -my-1.5 bg-gray-100 text-gray-400 hover:text-gray-900 rounded-lg focus:ring-2 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center justify-center h-8 w-8" aria-label="Close">
      <span class="sr-only">Close</span>
      <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
      </svg>
    </button>
  `;


    // Add the toast to the container
    this.toastContainer.appendChild(toast);

    // Add event listener for the close button
    const closeButton = toast.querySelector('button');
    closeButton?.addEventListener('click', () => {
      this.addFadeOutAnimation(toast);
    });
  
    // Remove toast after 2 seconds if not closed manually
    setTimeout(() => {
      if (toast.parentElement) {
        this.addFadeOutAnimation(toast);
      }
    }, 2000);
  }
  
  private addFadeOutAnimation(toast: HTMLElement) {
    toast.classList.add('toast-fade-out');
    // Remove the toast from DOM after animation ends
    toast.addEventListener('animationend', () => {
      toast.remove();
    });
  }

  private getToastClass(type: 'success' | 'error' | 'info') {
    switch (type) {
      case 'success':
        return 'bg-gradient-to-r from-white to-gray-100';
      case 'error':
        return 'bg-gradient-to-r from-white to-gray-100';
      case 'info':
        return 'bg-gradient-to-r from-white to-gray-100';
      default:
        return 'bg-gradient-to-r from-white to-gray-100';
    }
  }

  private getIconClass(type: 'success' | 'error' | 'info') {
    switch (type) {
      case 'success':
        return 'text-green-500';
      case 'error':
        return 'text-red-500';
      case 'info':
        return 'text-blue-500';
      default:
        return 'text-gray-500';
    }
  }

  private getTextClass(type: 'success' | 'error' | 'info') {
    switch (type) {
      case 'success':
        return 'text-black';
      case 'error':
        return 'text-black';
      case 'info':
        return 'text-black';
      default:
        return 'text-black';
    }
  }

  private getIcon(type: 'success' | 'error' | 'info') {
    switch (type) {
      case 'success':
        return `<svg class="w-6 h-6" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 12l4 4 8-8"/></svg>`;
      case 'error':
        return `<svg class="w-6 h-6" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>`;
      case 'info':
        return `<svg class="w-6 h-6" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 16v-4m0 0V8m0 4h.01M12 20h.01M12 4v1M12 4V3"/></svg>`;
      default:
        return '';
    }
  }
}
