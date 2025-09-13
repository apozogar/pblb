export interface User {
    sub: string; // El email del usuario (subject del token)
    authorities: { authority: string }[];
    iat: number; // Issued at
    exp: number; // Expiration time
}