package src_girela_castellon_ruben;

import java.util.*;
import java.util.Collections;
import java.util.concurrent.*;

import core.game.Observation;
import core.game.StateObservation;
import core.logging.Logger;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Utils;
import tracks.ArcadeMachine;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * 
 * @author Ruben Girela Castellón
 * Practice 1 (done)
 * - level 0 (simple deliberative agent)
 *
 */

public class myAgent extends AbstractPlayer{
	// 1) Busca la puerta mas cercana. 
	// 2) Escoge la ruta optima a ir a la puerta usando el algoritmo A*.
	Vector2d fescala;
	Vector2d portal;
	int orientacion_avatar;
	ArrayList<Nodo> abiertos;
	ArrayList<Nodo> cerrados;
	
	public ArrayList<String> path;
	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public myAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
        fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      
      
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos el portal mas proximo
        portal = posiciones[0].get(0).position;
        portal.x = Math.floor(portal.x / fescala.x);
        portal.y = Math.floor(portal.y / fescala.y);
        
        
        //si no hay objetos es el nivel 1
        if(stateObs.getResourcesPositions()==null)
        	//Calcula una ruta optima usando A*
        	path = algoritmoEstrella(stateObs, elapsedTimer);
        //si no tiene enemigos es el nivel 2
        else if(stateObs.getNPCPositions() == null)
        	path = algoritmoGreedyManhattan(stateObs, elapsedTimer, 9);
        
        
	}
	
	/**
	 * Apply the actions
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		
		//get avater position
		Vector2d avatar =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, 
        		stateObs.getAvatarPosition().y / fescala.y);
		
		//copia por referencia
		/*Vector2d n = avatar.copy();
		n.set(n.x+2, n.y+2);
		
		if(path.size()==0) {
			path.addElement(new Nodo(null, avatar,0,0,0));
			path.addElement(new Nodo(avatar, n,0,1,1));
			
			System.out.println(path.firstElement().hijo);
			path.remove(0);
			System.out.println(path.firstElement().hijo);
		}*/
		/*TimeUnit time = TimeUnit.SECONDS;
		long t = 1;
		try {
			time.sleep(t);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//System.out.println(orientacion(stateObs));
		
		if(path != null) {
			
			if(path.size()>0) {
				/*System.out.println(path.size());
				System.out.println(path);*/
				switch(path.get(path.size()-1)) {
					case "UP":
						//System.out.println("ARRIBA");
						if(orientacion(stateObs) == 0)
							path.remove(path.size()-1);
						return Types.ACTIONS.ACTION_UP;
					case "DOW":
						//System.out.println("ABAJO");
						if(orientacion(stateObs) == 1)
							path.remove(path.size()-1);
						return Types.ACTIONS.ACTION_DOWN;
					case "IZ":
						//System.out.println("IZQUIERDA");
						if(orientacion(stateObs) == 3)
							path.remove(path.size()-1);
						return Types.ACTIONS.ACTION_LEFT;
					case "DER":
						//System.out.println("DERECHA");
						if(orientacion(stateObs) == 2)
							path.remove(path.size()-1);
						return Types.ACTIONS.ACTION_RIGHT;
				}
			}		
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Es un algortimo Greedy usando la distancia Manhattan
	 * Lo he personalizado ya que la eleccion de la gema es Greedy utilizando la distancia Manhattan, 
	 * pero la ruta a seleccionar entre el avatar y la gema es el A*, con lo cual estoy combinando 2 algoritmos en 1.
	 * @param stateObs
	 * @param elapsedTimer
	 * @param max_diamont numero maximo de diamantes
	 * @return
	 */
	public ArrayList<String> algoritmoGreedyManhattan(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, int max_diamont) {
		int cont_diamont=0;//numero de gemas cogidas
		abiertos = new ArrayList<Nodo>();
		cerrados = new ArrayList<Nodo>();
		ArrayList<Vector2d> gemas_restantes = new ArrayList<Vector2d>();//lista de gemas no cogidas
		orientacion_avatar = orientacion(stateObs);
		Vector2d avatar = new Vector2d(stateObs.getAvatarPosition().x / fescala.x,stateObs.getAvatarPosition().y / fescala.y);
		//nodo actual de donde parte, en este caso la posicion del avatar
		Nodo nodo_actual = new Nodo(new Vector2d(-1, -1), avatar, 0, 0, calculateManhattan(avatar, portal));
		Nodo best_node = new Nodo();
		
		//nodo objetivo seleccionado para las gemas y el portal
		Vector2d nodo_objetivo = new Vector2d();
		Integer distancia_actual=0, distancia_min = 999, pos_cerrados = -1, pos_abiertos = -1;
		boolean exit = false;
		Vector<Pair> lista_sucesores = new Vector<Pair>();
		ArrayList<String> camino = new ArrayList<String>();
		ArrayList<String> sub_camino = new ArrayList<String>();
		
		Nodo inicial_N = nodo_actual.clone();
		Integer cont = 0;
		
		//System.out.println("max y: "+(stateObs.getObservationGrid()[0].length-1));
		//System.out.println("max x: "+(stateObs.getObservationGrid().length-1));
		
		//System.out.println(stateObs.getResourcesPositions()[0]);
		//añado las posiciones de las gemas
		for(Observation obs:stateObs.getResourcesPositions()[0]) {
			//System.out.println((int)(obs.position.x/fescala.x)+", "+(int)(obs.position.y/fescala.y));
			gemas_restantes.add(new Vector2d((obs.position.x/fescala.x),(int)(obs.position.y/fescala.y)));
			//System.out.println(stateObs.getObservationGrid()[(int)(obs.position.x)][(int)(obs.position.y)].get(0).category);
		}
		
		//System.out.println(gemas_restantes);
		//mientras no obtenga las 9 gemas y haya salido por la puerta
		while(!exit) {
			//si no ha cogido el numero maximo de gemas
			if(cont_diamont < max_diamont) {
				//selecciona la gema más cercana
				for(Vector2d gema:gemas_restantes) {
					distancia_actual = calculateManhattan(nodo_actual.hijo,gema);
					if(distancia_actual<distancia_min) {
						distancia_min = distancia_actual;
						nodo_objetivo = gema;
					}
				}
				distancia_min = 999;
			}
			//si se han cogido todas las gemas busco la ruta optima a la puerta
			if(cont_diamont>=max_diamont) {
				nodo_objetivo = portal;
				exit = true;
			}
			//calculo la distancia heuristica entre la posicion actual y la gema escogida o puerta 
			nodo_actual.h = calculateManhattan(nodo_actual.hijo,nodo_objetivo);
			//lo añado a la lista de abiertos
			abiertos.add(nodo_actual.clone());
			//System.out.println("Nodo objetivo: "+nodo_objetivo);
			//System.out.println("Nodo actual_gema_anterior: "+nodo_actual.hijo+", pad: "+nodo_actual.padre+", h = "+nodo_actual.h);
			
			while(!abiertos.isEmpty()) {
				
				/*System.out.println("--------------------------------------");
				for(Nodo nod:abiertos) {
					System.out.println("Padre: "+nod.padre+", Hijo: "+nod.hijo);
				}*/
				
				//save the best node
				best_node = bestNode();
				
				/*if(best_node.hijo.x != nodo_actual.hijo.x && best_node.hijo.y != nodo_actual.hijo.y && cont_diamont<max_diamont) {
					for(Vector2d gema:gemas_restantes) {
						distancia_actual = calculateManhattan(best_node.hijo,gema);
						if(calculateManhattan(best_node.hijo,gema)<distancia_min) {
							distancia_min = distancia_actual;
							nodo_objetivo = gema;
						}
					}
					distancia_min = 999;
				}*/
				
				//add the best node in abiertos and remove of cerrados
				cerrados.add(best_node);	
				abiertos.remove(abiertos.indexOf(best_node));
				
				//para saber la orientacion del avatar en cada movimiento
				if(best_node.hijo != avatar) {
					switch(best_node.orientacion) {
					case 0://arriba
						if(orientacion_avatar != 0)
							orientacion_avatar = 0;
						break;
					case 1://abajo
						if(orientacion_avatar != 1)
							orientacion_avatar = 1;
						break;
					case 2://derecha
						if(orientacion_avatar != 2)
							orientacion_avatar = 2;
						break;
					case 3://izquierda
						if(orientacion_avatar != 3)
							orientacion_avatar = 3;
						break;
					}
				}
				
				//System.out.println("Mejor nodo: "+best_node.hijo+", con padre: "+best_node.padre);
				
				//si el mejor nodo obtenido es el nodo objetivo termina
				if(best_node.hijo.x == nodo_objetivo.x && best_node.hijo.y == nodo_objetivo.y) { 
					abiertos.clear();
					/*System.out.println(nodo_objetivo);
					System.out.println(gemas_restantes.indexOf(nodo_objetivo));*/
					//si ha alcanzado una gema lo borra de la lista de gemas no cogidas aun
					if(gemas_restantes.indexOf(nodo_objetivo) != -1)
						gemas_restantes.remove(gemas_restantes.indexOf(nodo_objetivo));
					//System.out.println(gemas_restantes);
					//actualizo la posición actual
					nodo_actual.hijo = nodo_objetivo;
					
					++cont_diamont;//cuento la gema escogida
					
				}else {//en caso contrario expando los nodos vecinos o sucesores
					
					/*System.out.println("Coste cada paso: "+best_node.g+" + "+best_node.h+" = "+(best_node.g+best_node.h));
					System.out.println("Nodo Padre: "+best_node.hijo);*/
					
					//expand the nodes
					lista_sucesores=expandNodes(best_node, stateObs);
					
					//recorro los sucesores validos (que no sean muros y esten dentro de las dimensiones del mapa)
					for(Pair sucesor:lista_sucesores) {
						
						//si no es el nodo padre
						if(best_node.padre.x != sucesor.key.x || best_node.padre.y != sucesor.key.y) {
							//creo mi nodo personalizado pasandole el nodo padre, el nodo sucesor expandido, la orientacion de la casilla, g y h
							Nodo new_node = new Nodo(best_node.hijo, sucesor.key, sucesor.value,calculateG(best_node.g, sucesor.value, orientacion_avatar),calculateManhattan(sucesor.key, nodo_objetivo));
								
							//System.out.println("No es el padre --> Padre: "+new_node.padre+", Hijo: "+new_node.hijo+", h= "+new_node.h);
							//busco si esta ese nodo en la lista de abiertos o en cerrados 
							pos_abiertos = contiene(abiertos, new_node);
							pos_cerrados = contiene(cerrados, new_node);
							
							//si el sucesor esta ya en cerrados y la g que tiene en cerrados es > que la g del sucesor actual
							//es decir que tiene mejor coste que el que tiene guardado
							if(pos_cerrados != -1) {
								if(cerrados.get(pos_cerrados).g > new_node.g) {
									//lo actualizo
									cerrados.set(pos_cerrados, new_node);
									
									/*cerrados.remove(pos_cerrados);
									abiertos.add(new_node);*/
								}
							//si esta en la lista de abiertos y la g del sucesor es mejor que la que tiene en abiertos
							}else if(pos_abiertos != -1) {
								if(abiertos.get(pos_abiertos).g > new_node.g)
									abiertos.set(pos_abiertos, new_node);//actualizo el nodo hijo con el nuevo padre
								
							//if the new node hasn't been explored
							}else if(pos_abiertos == -1 && pos_cerrados == -1)
								abiertos.add(new_node);//adds it to the list of open nodes
							
						}
					}
					//System.out.println("Size abiertos: "+abiertos.size());
				}
				
				++cont;
			}
			
			/*System.out.println("Ruta -----------------------------------");
			for(Nodo n:cerrados)
				System.out.println("Padre: "+n.padre+", Hijo: "+n.hijo);*/
			
			//guardo el subcamino desde el nodo inicial hasta la gema o portal
			sub_camino=construirPath(cerrados,inicial_N,nodo_objetivo);
			//actualizo el nodo inicial a la de la gema o portal
			inicial_N = nodo_actual.clone();
			//System.out.println(inicial_N.hijo+" vs "+nodo_actual.hijo);
			/*System.out.println(sub_camino.size());
			System.out.println(sub_camino);*/
			
			//guardo el subcamino en el camino final
			for(int i = sub_camino.size()-1; i >=0; --i) {
				camino.add(sub_camino.get(i));
			}
			/*System.out.println(camino.size());
			System.out.println(camino);*/
			
			//limpio la lista de cerrados
			cerrados.clear();
			
			//System.out.println("Numero gemas: "+(cont_diamont));
		}
		//invierto el camino que esta al reves
		ArrayList<String> camino_inverso = new ArrayList<String>();
		for(int i = camino.size()-1; i>=0; --i) {
			camino_inverso.add(camino.get(i));
		}
		
		return camino_inverso;//y devuelvo la solución
	}
	
	
	/*
	 * Algortimo A* que calcula la ruta optima hacia el portal
	 */
	public ArrayList<String> algoritmoEstrella(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//created the vector of open and closed nodes
		abiertos = new ArrayList<Nodo>();//nodos explorados
		cerrados = new ArrayList<Nodo>();//nodos recorridos
		Vector<Pair> lista_sucesores;//nodos vecinos del mejor nodo encontrado
		Nodo best_node = new Nodo();//el mejor nodo encontrado hasta ahora
		Integer pos_abiertos=0, pos_cerrados=0;
		
		//get avater position
		Vector2d avatar =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, 
        		stateObs.getAvatarPosition().y / fescala.y);
		
		Nodo avatar_N = new Nodo(new Vector2d(-1, -1), avatar, orientacion_avatar, 0, calculateManhattan(avatar, portal));
		
		//obtengo la orientacion del avatar
		orientacion_avatar = orientacion(stateObs);
		
		//System.out.println("Orientacion antes (izquierda): " + orientacion_avatar);
		
		//add the first node (the current position of avatar)
		abiertos.add(avatar_N);
		
		//mientras no este vacio la lista de nodos explorados
		while(!abiertos.isEmpty()) {
			//save the best node
			best_node = bestNode();
			
			//add the best node in abiertos and remove of cerrados
			cerrados.add(best_node);	
			abiertos.remove(abiertos.indexOf(best_node));
			
			//si el mejor nodo no es el avatar obtengo la orientacion ficticia del avatar en la siguiente casilla
			/**
			 * esto me sirva para saber si da un paso o 2 pasos, es decir si tiene que cambiar de direccion tiene que hacer 2 movimientos
			 * - 1 para moverse a la orientación
			 * - 2 para ir a la casilla
			 */
			if(best_node.hijo != avatar) {
				switch(best_node.orientacion) {
				case 0://arriba
					if(orientacion_avatar != 0)
						orientacion_avatar = 0;
					break;
				case 1://abajo
					if(orientacion_avatar != 1)
						orientacion_avatar = 1;
					break;
				case 2://derecha
					if(orientacion_avatar != 2)
						orientacion_avatar = 2;
					break;
				case 3://izquierda
					if(orientacion_avatar != 3)
						orientacion_avatar = 3;
					break;
				}
			}
			//System.out.println("Orientacion despues: " + orientacion_avatar);
			
			//si el mejor nodo obtenido es el nodo objetivo termina
			if(best_node.hijo.x == portal.x && best_node.hijo.y == portal.y) { 
				
				/*System.out.println("PORTAL");
				System.out.println("Coste: "+best_node.g);*/

				//construyo el camino desde el portal al avatar y lo devuelvo
				return construirPath(cerrados,avatar_N,portal);
			}else {//en caso contrario expando los nodos vecinos o sucesores
				
				/*System.out.println("Coste cada paso: "+best_node.g+" + "+best_node.h+" = "+(best_node.g+best_node.h));
				System.out.println("Nodo Padre: "+best_node.hijo);*/
				
				//expand the nodes
				lista_sucesores=expandNodes(best_node, stateObs);
				
				//recorro los sucesores validos (que no sean muros y esten dentro de las dimensiones del mapa)
				for(Pair sucesor:lista_sucesores) {
					
					//si no es el nodo padre
					if(best_node.padre.x != sucesor.key.x || best_node.padre.y != sucesor.key.y) {
						//creo mi nodo personalizado pasandole el nodo padre, el nodo sucesor expandido, la orientacion de la casilla, g y h
						Nodo new_node = new Nodo(best_node.hijo, sucesor.key, sucesor.value,calculateG(best_node.g, sucesor.value, orientacion_avatar),calculateManhattan(sucesor.key, portal));
						
						//busco si esta ese nodo en la lista de abiertos o en cerrados 
						pos_abiertos = contiene(abiertos, new_node);
						pos_cerrados = contiene(cerrados, new_node);
						
						//si el sucesor esta ya en cerrados y la g que tiene en cerrados es > que la g del sucesor actual
						//es decir que tiene mejor coste que el que tiene guardado
						if(pos_cerrados != -1) {
							if(cerrados.get(pos_cerrados).g > new_node.g) {
								//lo actualizo
								cerrados.set(pos_cerrados, new_node);
								
								/*cerrados.remove(pos_cerrados);
								abiertos.add(new_node);*/
							}
						//si esta en la lista de abiertos y la g del sucesor es mejor que la que tiene en abiertos
						}else if(pos_abiertos != -1) {
							if(abiertos.get(pos_abiertos).g > new_node.g)
								abiertos.set(pos_abiertos, new_node);//actualizo el nodo hijo con el nuevo padre
							
						//if the new node hasn't been explored
						}else if(pos_abiertos == -1 && pos_cerrados == -1)
							abiertos.add(new_node);//adds it to the list of open nodes
						
					}
				}
				//System.out.println("Size abiertos: "+abiertos.size());
			}
		}
		
		//System.out.println("Tam cerrados: " + cerrados.size());
		
		//si llega aqui es que no hay solución
		return null;
	}
	
	/**
	 * Obtiene el mejor nodo
	 */
	public Nodo bestNode() {
		//save the best node
		Nodo best_node = new Nodo();
		
		Collections.sort(abiertos);//ordeno la lista de menor a mayor f
		
		//System.out.println("Siguiente nodo----------------------------------");
		
		//obtengo el mejor nodo
		best_node = abiertos.get(0);
		//System.out.println("min f = "+(best_node.g+best_node.h));
		
		//return the best node
		return best_node;
	}
	/**
	 * gets the Manhattan distance
	 * @param current_node current node to calculate distance h 
	 */
	public Integer calculateManhattan(Vector2d current_node, Vector2d objetivo){
		//Manhattan distance
        Integer distance = 0;        
        
        //calculate the Manhattan distance 
        //distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y)));
        distance = ((int) (Math.abs(current_node.x - objetivo.x) + Math.abs(current_node.y-objetivo.y)));
        
        return distance;
        
	}
	
	/**
	 * Otiene los nodos sucesores del nodo padre
	 * @param node es el nodo padre a expandir
	 * @param stateObs nos sirve para saber si sobre pasa los margenes del mapa
	 * @return
	 */
	public Vector<Pair> expandNodes(Nodo node, StateObservation stateObs){
		
		Vector2d hijo;//nodo hijo obtenido del nodo padre
		ArrayList<Observation> casilla;//tipo de casilla
		Integer rest_x=0, rest_y=0;//para obtener las 4 casillas (UP, DOWN, LEFT, RIGHT)
		boolean not_wall = false;//Nos indica si es un muro o no
		boolean dimension = false;//para no sobrepasar los margenes del mapa
		
		Vector<Pair> sucesores = new Vector<Pair>();
		
		//para obtener el tipo de superficie utilizar esto:
		//stateObs.getObservationGrid()[(int)(portal.x)][(int)(portal.y)]);
		
		//[Observation{category=6, itype=9, obsID=99, position=16.0 : 32.0, reference=-1.0 : -1.0, sqDist=1378.0}]
		// esto devuelve un arraylist, pero solo nos interesa category
		
		//itero las 4 posiciones
		for(Integer i = 0; i < 4; ++i) {
			//dependiendo de la posicion i es una direccion u otra
			switch(i) {
				case 0://arriba
					//System.out.println("ARRIBA");
					if(node.hijo.y - 1 >= 0)//si no supera los margenes del mapa
						dimension = true;//lo activo
					
					//si esta activo guardo lo que tiene que restar para ir a la casilla sucesora
					if(dimension) {
						rest_y = -1;
						rest_x = 0;
					}
					break;
				case 1://abajo
					//System.out.println("ABAJO");
					if(node.hijo.y + 1 <= stateObs.getObservationGrid()[0].length-1)
						dimension = true;
					
					if(dimension) {
						rest_y = 1;
						rest_x = 0;
					}
					break;
				case 2://izquierda
					//System.out.println("IZQUIERDA");
					if (node.hijo.x - 1 >= 0) 
						dimension = true;
					
					if(dimension) {
						rest_y = 0;
						rest_x = -1;
					}
					break;
				case 3://derecha
					//System.out.println("DERECHA");
					if (node.hijo.x + 1 <= stateObs.getObservationGrid().length - 1)
						dimension = true;
					
					if(dimension) {
						rest_y = 0;
						rest_x = 1;
					}
					break;
				default:
					rest_y = rest_x = 0;
					break;	
			}
			
			if (dimension) {//si no sobrepasa los margenes del mapa
				
				//save the new node
				hijo = new Vector2d(node.hijo.x+rest_x, node.hijo.y+rest_y);
				
				//save the properties of the box
				casilla = stateObs.getObservationGrid()[(int)(hijo.x)][(int)(hijo.y)];
					
				//if not empty
				if(casilla.size()>0) {
					//check if it isn't a wall
					if(casilla.get(0).category != 4)
						not_wall = true;
				}else {//si esta vacio se interpreta que no es un muro
					not_wall = true;
				}
				
				//si no es un muro
				if(not_wall) {
					//añado el sucesor a la lista de sucesores
					sucesores.add(new Pair(hijo, i));
					
					/*System.out.print("Sucessor: "+hijo);
					switch(i) {
						case 0://arriba
							System.out.println(", ARRIBA");
							break;
						case 1://abajo
							System.out.println(", ABAJO");
							break;
						case 2://izquierda
							System.out.println(", IZQUIERDA");
							break;
						case 3://derecha
							System.out.println(", DERECHA");
							break;
					}*/
				}
	        }
			//reseteo para el siguiente sucesor
			not_wall = false;
			dimension = false;
		}
		
		return sucesores;//devuelvo la lista de sucesores
	}
	
	/**
	 * Duelvo la orientacion del avatar en un valor entero
	 * @param stateObs para obtener la orientacion del avatar
	 * @return Integer (0, 1, 2 o 3)
	 */
	public int orientacion(StateObservation stateObs) {
		/*
		 * UP --> (0, -1) --> 0
		 * DOWN --> (0, 1) --> 1
		 * RIGHT --> (-1, 0) --> 3
		 * LEFT --> (1, 0) --> 2
		 */
		//System.out.println("ORIENTACION: " + stateObs.getAvatarOrientation());
		if(stateObs.getAvatarOrientation().x == 0 && stateObs.getAvatarOrientation().y == -1) 
			return 0;//UP
		else if(stateObs.getAvatarOrientation().x == 0 && stateObs.getAvatarOrientation().y == 1)
			return 1;//DOWN
		else if(stateObs.getAvatarOrientation().x == -1 && stateObs.getAvatarOrientation().y == 0)
			return 3;//RIGHT
		return 2;//LEFT
	}
	
	/**
	 * Funcion que calcula la posicion de la casilla sucesora, esta funcion se usa cuando crea el path cuando obtiene la solución
	 * @param nod1 es el nodo padre
	 * @param nod2 es el nodo hijo del padre
	 * @return el desplazamiento hacia esa casilla
	 */
	public String orientacionCasilla(Vector2d nod1, Vector2d nod2) {
		String dir = "";
		
		if((nod1.x-1) == nod2.x && nod1.y == nod2.y)
			dir = "IZ";
		else if((nod1.x+1) == nod2.x && nod1.y == nod2.y)
			dir = "DER";
		else if(nod1.x == nod2.x && (nod1.y-1) == nod2.y)
			dir="UP";
		else if(nod1.x == nod2.x && (nod1.y+1) == nod2.y)
			dir="DOW";
		//System.out.println(dir);
		return dir;
	}
	
	/**
	 * calcula la g del nodo sucesor 
	 * @param g coste del camino real hasta ahora
	 * @param orientacion_casilla donde esta posicionada la casilla
	 * @param orientacion_avatar donde esta orientado el avatar en la casilla padre de la casilla sucesora
	 * @return el coste real de la casilla sucesora
	 */
	public int calculateG(int g, int orientacion_casilla, int orientacion_avatar) {
		
		int new_g = g;//guardo la g del nodo padre
		
		//y calculo la g del nodo sucesor
		//si esta en la misma orientacion que el avatar el coste es 1
		if(orientacion_avatar == orientacion_casilla)
			++new_g;
		else//en caso contrario el coste es 2 (1 para mover la orientacion y 2 para ir a la casilla)
			new_g +=2;		
		
		return new_g;
	}
	
	/**
	 * Comprueba si el nodo esta en la lista (abiertos o cerrados)
	 * @param lista lista de nodos
	 * @param node nodo a buscar en la lista
	 * @return devuelve la posición de la lista o -1 si no la ha encontrado
	 */
	public int contiene(ArrayList<Nodo> lista, Nodo node) {
		
		//si el nodo esta en la lista devuelvo la posición
		for(Integer i = 0; i < lista.size(); ++i) {
			if(lista.get(i).hijo.x == node.hijo.x && lista.get(i).hijo.y == node.hijo.y) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Construye el camino desde el portal al avatar
	 * @param lista_cerrados obtengo la lista de nodos recorridos (cerrados)
	 * @param nodo_inicial obtengo el nodo inicial (avatar)
	 * @param objetivo obtengo el nodo objetivo (portal)
	 * @return devuelvo el camino que realizara el avatar
	 */
	public ArrayList<String> construirPath(ArrayList<Nodo> lista_cerrados, Nodo nodo_inicial, Vector2d objetivo){
		ArrayList<String> pasos = new ArrayList<String>();//pasos del avatar
		Nodo next_nodo = new Nodo();//guardo el siguiente nodo
		next_nodo.hijo = objetivo;//guardo el nodo objetivo para buscar el padre de ese nodo
		
		//System.out.println("Portal: "+objetivo);
		
		while(next_nodo.hijo.x != -1){//mientras no encuentre el nodo inicial (avatar)
			//System.out.println("Hijo: "+next_nodo.hijo);
			//recorre los nodos de la lista de cerrados
			for(Nodo nod:lista_cerrados) {
				//System.out.println("Nodos: hijo "+nod.hijo+", padre "+nod.padre);
				//si encuentra el hijo del nodo a buscar
				if(nod.hijo.x == next_nodo.hijo.x && nod.hijo.y == next_nodo.hijo.y) {
					/*System.out.println("Encontrado Padre: " + nod.padre + ", Hijo: "+nod.hijo);
					System.out.println("Nodo Inicial Padre: " + nodo_inicial.padre + ", Hijo: "+nodo_inicial.hijo);*/
					/**
					 * si el nodo padre no es -1 quiere decir que hemos llegado al nodo inicial (avatar)
					 * ya que las posiciones son positivas y una posición negativa quiere decir que no tiene padre.
					 * Dicho de otro modo guardo las acciones que realizara salvo la de la casilla inicial
					 */
					if(nod.padre.x != nodo_inicial.padre.x || nod.padre.y != nodo_inicial.padre.y) {
						//System.out.println("No es el inicio");
						//añado el paso a realizar
						pasos.add(orientacionCasilla(nod.padre,nod.hijo));
					}
					//guardo el padre del nodo hijo encontrado
					next_nodo.hijo = nod.padre;
					
				}
			}
		}
		return pasos;
	}
}
/**
 * class Nodo containing:
 * @param padre: node of parent
 * @param hijo: parent child node
 * @param orientacion: cell orientation relative to avatar orientation
 * @param g: real cost
 * @param h: heuristic cost
 * @author ruben girela castellon
 *
 */
class Nodo implements Comparable<Nodo>{
	
	public Vector2d padre;//guardo el nodo padre
	public Vector2d hijo;//guardo el nodo hijo
	public int orientacion;//guardo la orientacion de la casilla
	public int g;//el coste real
	public int h;//el coste heuristico
	
	public Nodo() {//inicializo el nodo vacio
		padre = new Vector2d();
		hijo = new Vector2d();
		orientacion = -1;
		h = g = 0;
	}
	/**
	 * Inicializo el nodo con los valores que recibe
	 * @param padre a guardar
	 * @param hijo a guardar
	 * @param orientacion del nodo a guardar
	 * @param g real a guardar
	 * @param h heuristico a guardar
	 */
	public Nodo(Vector2d padre, Vector2d hijo, int orientacion, int g, int h) {
		this.padre = padre;
		this.hijo = hijo;
		this.orientacion = orientacion;
		this.g = g;
		this.h = h;
	}
	/**
	 * Comparador para la ordenación de la lista de abiertos
	 * Lo ordena por coste f de menor a mayor
	 * @param otherNode obtiene el segundo nodo a comparar
	 * Es decir nodo1.compareTo(nodo2)
	 */
	public int compareTo(Nodo otherNode) {
		//calculo ambos costes f
        float f1 = g + h;
        float f2 = otherNode.g + otherNode.h;
        
        //y los comparo
        if (f1 < f2) {
                return -1;
        } else if (f1 > f2) {
                return 1;
        } else {
                return 0;
        }
	}
	public Nodo clone() {
		Nodo nodo = new Nodo(padre,hijo,orientacion,g,h);
		
		return nodo;
	}
};

/**
 * esto lo hago para los nodos expandidos o nodos sucesores, ya que tengo que guardar la orientacion 
 * @author ruben girela castellon
 * 
 */
class Pair{
	public Vector2d key;//nodo
	public Integer value;//orientacion
	/**
	 * Inicializo con los valores que recibe
	 * @param k
	 * @param v
	 */
	Pair(Vector2d k, Integer v){
		key = k;
		value = v;
	}
	/**
	 * Inicializo los valores vacios
	 */
	Pair(){
		key = new Vector2d();
		value = -1;
	}
}
