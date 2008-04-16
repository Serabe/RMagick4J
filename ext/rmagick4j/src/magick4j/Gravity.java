package magick4j;

public enum Gravity{

    CENTER{
        int getWidth(MagickImage img){ return img.getWidth()/2; }
        int getHeight(MagickImage img){ return img.getHeight()/2; }
    },
    
    
    EAST{
        int getWidth(MagickImage img){ return img.getWidth(); }
        int getHeight(MagickImage img){ return img.getHeight()/2; }
    },
    
    FORGET{ // TODO Implement?
        int getWidth(MagickImage img){ return 0; }
        int getHeight(MagickImage img){ return 0; }
    },
    
    NORTH{
        int getWidth(MagickImage img){ return img.getWidth()/2 ; }
        int getHeight(MagickImage img){ return 0; }
    },
    
    NORTH_EAST{
        int getWidth(MagickImage img){ return img.getWidth(); }
        int getHeight(MagickImage img){ return 0; }
    },
    
    NORTH_WEST{
        int getWidth(MagickImage img){ return 0; }
        int getHeight(MagickImage img){ return 0; }
    },
    
    SOUTH{
        int getWidth(MagickImage img){ return img.getWidth()/2; }
        int getHeight(MagickImage img){ return img.getHeight(); }
    },
    
    SOUTH_EAST{
        int getWidth(MagickImage img){ return img.getWidth(); }
        int getHeight(MagickImage img){ return img.getHeight(); }
    },
    
    SOUTH_WEST{
        int getWidth(MagickImage img){ return 0; }
        int getHeight(MagickImage img){ return img.getHeight(); }
    },
    
    WEST{
        int getWidth(MagickImage img){ return 0; }
        int getHeight(MagickImage img){ return img.getHeight()/2; }
    };
    
    abstract int getWidth(MagickImage img);
    
    abstract int getHeight(MagickImage img);
    
}