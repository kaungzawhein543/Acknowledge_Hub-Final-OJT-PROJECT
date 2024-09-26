export interface feedbackResponse {
    id: number;
    content?: string;
    staffName?: string;
    reply?: string;
    replyBy?: number;
    showInput?: boolean;
    replyText?: string;
    createdAt :string;
    replyAt : string;
    announcementId : number;
    photoPath : string;
    replyPhotoPath : string
}
