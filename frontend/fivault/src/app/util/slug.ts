export class SlugGenerator {
  
  static generateSlug(name: string): string {
    if (!name || name.trim() === '') {
      return '';
    }
    
    // Normalize and remove accents
    const normalized = name
      .normalize('NFD')                    // Canonical Decomposition
      .replace(/[\u0300-\u036f]/g, '');   // Remove diacritical marks
    
    // Generate slug (matching Java logic exactly)
    const slug = normalized
      .toLowerCase()
      .replace(/[^a-z0-9\-\s]/g, '')      // Keep only letters, numbers, hyphens, spaces
      .replace(/[\s]+/g, '-')              // Replace spaces with hyphens
      .replace(/-{2,}/g, '-')              // Collapse multiple hyphens
      .replace(/^-|-$/g, '');              // Trim hyphens from start/end
    
    return slug;
  }
}