// Esta interfaz sirve para que la vista de cuentas
// se actualice después de cada transacción para mostrar
// el nuevo saldo
package com.unicatolica.banco.vistas.eventos;

@FunctionalInterface
public interface TransaccionListener {
    void onTransaccionRealizada();
}