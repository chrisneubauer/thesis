FOR %%L IN (Arial, TimesNewRoman, Calibri, Helvetica, GillSans) DO (
	if not exist %cd%\%%L md %cd%\%%L
	FOR %%G IN (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z) DO (
		if not exist %cd%\%%L\%%G md %cd%\%%L\%%G
	)
)