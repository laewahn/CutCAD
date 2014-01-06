class Material 
{
	private String name;
	private int thickness;
	private int materialColor;
	private int power;
	private int speed;
	private int focus;
	private int frequency;
  
  	Material(String materialName, int thickness, int materialColor, int power, int speed, int focus, int frequency)
	{
		this.name = materialName;
		this.materialColor = materialColor;
		this.thickness = thickness;
		this.power = power;
		this.speed = speed;
		this.focus = focus;
		this.frequency = frequency;
	}

	public String getMaterialName()
	{
		return (name + " " + thickness + " mm");
	}

	public int getMaterialThickness()
	{
		return thickness;
	}

	public int getMaterialColor()
	{
		return materialColor;
	}

	public int getPower()
	{
	 	return power;
	}

	public int getSpeed()
	{
	 	return speed;
	}

	public int getFocus()
	{
	 	return focus;
	}

	public int getFrequency()
	{
	 	return frequency;
	}

//UNUSED
// Papier(120gr)                      100    7   500
// Fotokarton 0,4mm(160gr)            100   25   500
// Grafikkarton 1mm                   100   50   500
// Braunpappe 1mm                     100   55  2500
// Bristolkarton 0,6mm                100   18  2500
// Finnpappe 3mm (ds weiss_kaschiert) 100  100  5000 //Freq
// Filz(Girlsday_Blume)               100   40  5000
// Flexfolie (T-Shirt druck)           30    5  5000
// Flockfolie (T-Shirt druck)          50    5  5000
// Klebefolie,schwarz(auf 3D-Drucker) 100   30   300
// Kopierfolie(Soennecken) 0,1mm      100    8  5000
// Leder                              100   27  2500 //Thickness??    
// Kraftplex FO 3.0mm                  50  100   500
// Moosgummi,braun 3mm              100*2  20*2 2500 //Freq
// PP Folie Priplak 0,5mm             100   25  2500 //Freq??
// Polyester Vivak 0,5mm              100   23  5000					//???
// Polyester Vivak 0,8mm              100   20  5000					//????
// Polyester Vivak 1mm                100   39  5000					//????     
// Nessel(aus Baumwolle)              100   25  2500
// Organza(Stoff)                     100   10  5000     
// elring AbilN(Pappeart)             100   50  5000
// Textil Curv,Textil Membran         100   10  5000
// Gummi,schwarz 2mm!                 100   20  2500
// Kapton(Makerbot)                   100   30  5000
// Rubber                              15   10   100
// Dicke Wellpappe                    100   70  5000 //Thickness
// Acrylglas 1,6mm                     95   88  5000
// Plexiglass 3mm                      30   70  5000
// Plexiglass 5mm                      22   95  5000
// Papier                             100   10   500 
// Karton                             100   35   500 
// Karton(300gr,schwarz,dick)         100   40   500
// Holzklötze Friedenstisch         25-30   80   500
// Expo Giveaway Pappe                100   12   500
// Laseracryl (Nametags)              100   50  5000
// Laseracryl (>3mm)                   50  100  5000
// Acrylic-Plastik (Corona)            50   70  5000   
// *Plexiglass 6mm                     15  100  5000      
// *Plexiglass 10mm                    10  100  5000
// *PS Polystyrol 1mm                 100   45   500      
// *Polystyrol (PS Weiss) 1mm         100   65  5000
// Kokosschale 3-4mm                100*3 100*3  500
// *Graupappe 3mm                      60  100  2500      
// *Graupappe 2,5mm                    70  100  2500      
// *Graupappe 2mm                      85   90  2500      
// *Graupappe 1,5mm                   100   60  2500
// *Graupappe 1mm                     100   45  2500
// *Graupappe 0,5mm                   100   25  2500
// Kunstleder (Sarahs Kalender)       100  30  ??  ??  ??

}

