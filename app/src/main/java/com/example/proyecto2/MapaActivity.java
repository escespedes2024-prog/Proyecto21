package com.example.proyecto2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;

public class MapaActivity extends AppCompatActivity {

    private MapView mapView;
    private DBUser dbUser;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private int idIglesia = 1;
    private MyLocationNewOverlay myLocationOverlay;
    private static final double LATITUD_DEFECTO = -17.755844;
    private static final double LONGITUD_DEFECTO = -63.182488;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configurar osmdroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue("com.example.proyecto2");
        
        // Configurar directorio de caché
        File osmdroidBasePath = new File(getCacheDir(), "osmdroid");
        osmdroidBasePath.mkdirs();
        Configuration.getInstance().setOsmdroidBasePath(osmdroidBasePath);
        
        File osmdroidTileCache = new File(osmdroidBasePath, "tiles");
        osmdroidTileCache.mkdirs();
        Configuration.getInstance().setOsmdroidTileCache(osmdroidTileCache);
        
        setContentView(R.layout.activity_mapa);

        dbUser = new DBUser(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtener el mapa
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Configurar controlador del mapa
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Configurar ubicación en el mapa
        configurarMapa();
    }

    private void configurarMapa() {
        // Obtener información de la iglesia
        Iglesia iglesia = dbUser.obtenerIglesia(idIglesia);
        
        IMapController mapController = mapView.getController();
        GeoPoint ubicacion;
        
        if (iglesia != null && iglesia.getLatitud() != 0 && iglesia.getLongitud() != 0) {
            // Usar ubicación de la iglesia
            ubicacion = new GeoPoint(iglesia.getLatitud(), iglesia.getLongitud());
            
            // Agregar marcador
            Marker marcador = new Marker(mapView);
            marcador.setPosition(ubicacion);
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marcador.setTitle(iglesia.getNombre());
            if (iglesia.getDireccion() != null && !iglesia.getDireccion().isEmpty()) {
                marcador.setSnippet(iglesia.getDireccion());
            }
            mapView.getOverlays().add(marcador);
        } else {
            // Usar ubicación por defecto
            ubicacion = new GeoPoint(LATITUD_DEFECTO, LONGITUD_DEFECTO);
            
            // Agregar marcador
            Marker marcador = new Marker(mapView);
            marcador.setPosition(ubicacion);
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marcador.setTitle("Ubicación de la Iglesia");
            marcador.setSnippet("Configura la ubicación en Configuración");
            mapView.getOverlays().add(marcador);
            
            // Actualizar la iglesia con la ubicación por defecto
            if (iglesia == null) {
                iglesia = new Iglesia();
                iglesia.setId(idIglesia);
                iglesia.setNombre("Iglesia Principal");
                iglesia.setLatitud(LATITUD_DEFECTO);
                iglesia.setLongitud(LONGITUD_DEFECTO);
                dbUser.insertarIglesia(iglesia);
            } else {
                iglesia.setLatitud(LATITUD_DEFECTO);
                iglesia.setLongitud(LONGITUD_DEFECTO);
                dbUser.actualizarIglesia(iglesia);
            }
        }
        
        // Centrar el mapa en la ubicación
        mapController.setCenter(ubicacion);

        // Configurar mi ubicación si se tiene permiso
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            configurarMiUbicacion();
        }
    }

    private void configurarMiUbicacion() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                configurarMiUbicacion();
            } else {
                Toast.makeText(this, "Se necesita permiso de ubicación para mostrar tu ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}
