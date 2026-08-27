import { animate, state, style, transition, trigger, } from '@angular/animations';
/**
 * Animations used by MatTooltip.
 * @docs-private
 */
export const matTooltipAnimations = {
    /** Animation that transitions a tooltip in and out. */
    tooltipState: trigger('state', [
        // TODO(crisbeto): these values are based on MDC's CSS.
        // We should be able to use their styles directly once we land #19432.
        state('initial, void, hidden', style({ opacity: 0, transform: 'scale(0.8)' })),
        state('visible', style({ transform: 'scale(1)' })),
        transition('* => visible', animate('150ms cubic-bezier(0, 0, 0.2, 1)')),
        transition('* => hidden', animate('75ms cubic-bezier(0.4, 0, 1, 1)')),
    ]),
};
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoidG9vbHRpcC1hbmltYXRpb25zLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vcHJvamVjdHMvZXh0ZW5zaW9ucy90b29sdGlwL3Rvb2x0aXAtYW5pbWF0aW9ucy50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQ0wsT0FBTyxFQUVQLEtBQUssRUFDTCxLQUFLLEVBQ0wsVUFBVSxFQUNWLE9BQU8sR0FDUixNQUFNLHFCQUFxQixDQUFDO0FBRTdCOzs7R0FHRztBQUNILE1BQU0sQ0FBQyxNQUFNLG9CQUFvQixHQUU3QjtJQUNGLHVEQUF1RDtJQUN2RCxZQUFZLEVBQUUsT0FBTyxDQUFDLE9BQU8sRUFBRTtRQUM3Qix1REFBdUQ7UUFDdkQsc0VBQXNFO1FBQ3RFLEtBQUssQ0FBQyx1QkFBdUIsRUFBRSxLQUFLLENBQUMsRUFBRSxPQUFPLEVBQUUsQ0FBQyxFQUFFLFNBQVMsRUFBRSxZQUFZLEVBQUUsQ0FBQyxDQUFDO1FBQzlFLEtBQUssQ0FBQyxTQUFTLEVBQUUsS0FBSyxDQUFDLEVBQUUsU0FBUyxFQUFFLFVBQVUsRUFBRSxDQUFDLENBQUM7UUFDbEQsVUFBVSxDQUFDLGNBQWMsRUFBRSxPQUFPLENBQUMsa0NBQWtDLENBQUMsQ0FBQztRQUN2RSxVQUFVLENBQUMsYUFBYSxFQUFFLE9BQU8sQ0FBQyxpQ0FBaUMsQ0FBQyxDQUFDO0tBQ3RFLENBQUM7Q0FDSCxDQUFDIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHtcbiAgYW5pbWF0ZSxcbiAgQW5pbWF0aW9uVHJpZ2dlck1ldGFkYXRhLFxuICBzdGF0ZSxcbiAgc3R5bGUsXG4gIHRyYW5zaXRpb24sXG4gIHRyaWdnZXIsXG59IGZyb20gJ0Bhbmd1bGFyL2FuaW1hdGlvbnMnO1xuXG4vKipcbiAqIEFuaW1hdGlvbnMgdXNlZCBieSBNYXRUb29sdGlwLlxuICogQGRvY3MtcHJpdmF0ZVxuICovXG5leHBvcnQgY29uc3QgbWF0VG9vbHRpcEFuaW1hdGlvbnM6IHtcbiAgcmVhZG9ubHkgdG9vbHRpcFN0YXRlOiBBbmltYXRpb25UcmlnZ2VyTWV0YWRhdGE7XG59ID0ge1xuICAvKiogQW5pbWF0aW9uIHRoYXQgdHJhbnNpdGlvbnMgYSB0b29sdGlwIGluIGFuZCBvdXQuICovXG4gIHRvb2x0aXBTdGF0ZTogdHJpZ2dlcignc3RhdGUnLCBbXG4gICAgLy8gVE9ETyhjcmlzYmV0byk6IHRoZXNlIHZhbHVlcyBhcmUgYmFzZWQgb24gTURDJ3MgQ1NTLlxuICAgIC8vIFdlIHNob3VsZCBiZSBhYmxlIHRvIHVzZSB0aGVpciBzdHlsZXMgZGlyZWN0bHkgb25jZSB3ZSBsYW5kICMxOTQzMi5cbiAgICBzdGF0ZSgnaW5pdGlhbCwgdm9pZCwgaGlkZGVuJywgc3R5bGUoeyBvcGFjaXR5OiAwLCB0cmFuc2Zvcm06ICdzY2FsZSgwLjgpJyB9KSksXG4gICAgc3RhdGUoJ3Zpc2libGUnLCBzdHlsZSh7IHRyYW5zZm9ybTogJ3NjYWxlKDEpJyB9KSksXG4gICAgdHJhbnNpdGlvbignKiA9PiB2aXNpYmxlJywgYW5pbWF0ZSgnMTUwbXMgY3ViaWMtYmV6aWVyKDAsIDAsIDAuMiwgMSknKSksXG4gICAgdHJhbnNpdGlvbignKiA9PiBoaWRkZW4nLCBhbmltYXRlKCc3NW1zIGN1YmljLWJlemllcigwLjQsIDAsIDEsIDEpJykpLFxuICBdKSxcbn07XG4iXX0=