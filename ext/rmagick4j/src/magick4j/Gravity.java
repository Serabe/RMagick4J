package magick4j;

public enum Gravity{
    CENTER{
        int getX(MagickImage img){ return img.getWidth()/2; }
        
        int getX(MagickImage img, int consideredWidth){
            return (img.getWidth()-consideredWidth)/2;
        }
        
        int getY(MagickImage img){ return img.getHeight()/2; }
        
        int getY(MagickImage img, int consideredHeight){
            return (img.getHeight()-consideredHeight)/2;
        }
    },
    
    EAST{
        int getX(MagickImage img){ return img.getWidth(); }
        
        int getX(MagickImage img, int consideredWidth){
            return img.getWidth()-consideredWidth;
        }
        
        int getY(MagickImage img){ return img.getHeight()/2; }
        
        int getY(MagickImage img, int consideredHeight){
            return (img.getHeight()-consideredHeight)/2;
        }
    },
    
    FORGET{
        int getX(MagickImage img){ return 0; }
        int getX(MagickImage img, int consideredWidth){ return 0; }
        int getY(MagickImage img){ return 0; }
        int getY(MagickImage img, int consideredHeight){ return 0; }
    },
    
    NORTH{
        int getX(MagickImage img){ return img.getWidth()/2; }
        
        int getX(MagickImage img, int consideredWidth){
            return (img.getWidth()-consideredWidth)/2;
        }
        
        int getY(MagickImage img){ return 0; }
        int getY(MagickImage img, int consideredHeight){ return 0; }
    },
    
    NORTH_EAST{
        int getX(MagickImage img){ return img.getWidth(); }
        
        int getX(MagickImage img, int consideredWidth){
            return img.getWidth()-consideredWidth;
        }
        
        int getY(MagickImage img){ return 0; }
        int getY(MagickImage img, int consideredHeight){ return 0; }
    },
    
    NORTH_WEST{
        int getX(MagickImage img){ return 0; }
        int getX(MagickImage img, int consideredWidth){ return 0; }
        int getY(MagickImage img){ return 0; }
        int getY(MagickImage img, int consideredHeight){ return 0; }
    },
    
    SOUTH{
        int getX(MagickImage img){ return img.getWidth()/2; }
        
        int getX(MagickImage img, int consideredWidth){
            return (img.getWidth()-consideredWidth)/2;
        }
        
        int getY(MagickImage img){ return img.getHeight(); }
        
        int getY(MagickImage img, int consideredHeight){
            return img.getHeight()-consideredHeight;
        }
    },
    
    SOUTH_EAST{
        int getX(MagickImage img){ return img.getWidth(); }
        
        int getX(MagickImage img, int consideredWidth){
            return img.getWidth()-consideredWidth;
        }
        
        int getY(MagickImage img){ return img.getHeight(); }
        
        int getY(MagickImage img, int consideredHeight){
            return img.getHeight()-consideredHeight;
        }
    },
    
    SOUTH_WEST{
        int getX(MagickImage img){ return 0; }
        int getX(MagickImage img, int consideredWidth){ return 0; }
        int getY(MagickImage img){ return img.getHeight(); }
        
        int getY(MagickImage img, int consideredHeight){
            return img.getHeight()-consideredHeight;
        }
    },
    
    WEST{
        int getX(MagickImage img){ return 0; }
        int getX(MagickImage img, int consideredWidth){ return 0; }
        int getY(MagickImage img){ return img.getHeight()/2; }
        
        int getY(MagickImage img, int consideredHeight){
            return (img.getHeight()-consideredHeight)/2;
        }
    };
    
    abstract int getX(MagickImage img);
    
    abstract int getX(MagickImage img, int consideredWidth);
    
    abstract int getY(MagickImage img);
    
    abstract int getY(MagickImage img, int consideredHeight);
    
}