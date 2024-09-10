(window as any).global = window;
if (typeof window !== 'undefined' && window.indexedDB) {
} else {
    console.error('IndexedDB is not available.');
}