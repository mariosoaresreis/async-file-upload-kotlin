package com.marioreis.utils

import java.util.*
import kotlin.math.floor

class GeraCpfCnpj {
    private var comPontos = false

    private fun randomiza(n: Int): Int {
        val ranNum = (Math.random() * n).toInt()
        return ranNum
    }

    val lexicon: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890"

    val rand: Random = Random()

    // consider using a Map<String,Boolean> to say whether the identifier is being used or not
    val identifiers: Set<String> = HashSet()

    fun randomIdentifier(): String {
        var builder = StringBuilder()
        while (builder.toString().length == 0) {
            val length = rand.nextInt(5) + 5
            for (i in 0 until length) {
                builder.append(lexicon[rand.nextInt(lexicon.length)])
            }
            if (identifiers.contains(builder.toString())) {
                builder = StringBuilder()
            }
        }
        return builder.toString()
    }

    private fun mod(dividendo: Int, divisor: Int): Int {
        return Math.round(dividendo - (floor((dividendo / divisor).toDouble()) * divisor)).toInt()
    }

    fun cpf(): String {
        val n = 9
        val n1 = randomiza(n)
        val n2 = randomiza(n)
        val n3 = randomiza(n)
        val n4 = randomiza(n)
        val n5 = randomiza(n)
        val n6 = randomiza(n)
        val n7 = randomiza(n)
        val n8 = randomiza(n)
        val n9 = randomiza(n)
        var d1 = n9 * 2 + n8 * 3 + n7 * 4 + n6 * 5 + n5 * 6 + n4 * 7 + n3 * 8 + n2 * 9 + n1 * 10

        d1 = 11 - (mod(d1, 11))

        if (d1 >= 10) d1 = 0

        var d2 = d1 * 2 + n9 * 3 + n8 * 4 + n7 * 5 + n6 * 6 + n5 * 7 + n4 * 8 + n3 * 9 + n2 * 10 + n1 * 11

        d2 = 11 - (mod(d2, 11))

        var retorno: String? = null

        if (d2 >= 10) d2 = 0
        retorno = ""

        retorno = if (comPontos) "$n1$n2$n3.$n4$n5$n6.$n7$n8$n9-$d1$d2"
        else "" + n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + d1 + d2

        return retorno
    }

    fun cnpj(): String {
        val n = 9
        val n1 = randomiza(n)
        val n2 = randomiza(n)
        val n3 = randomiza(n)
        val n4 = randomiza(n)
        val n5 = randomiza(n)
        val n6 = randomiza(n)
        val n7 = randomiza(n)
        val n8 = randomiza(n)
        val n9 = 0 //randomiza(n);
        val n10 = 0 //randomiza(n);
        val n11 = 0 //randomiza(n);
        val n12 = 1 //randomiza(n);
        var d1 =
            n12 * 2 + n11 * 3 + n10 * 4 + n9 * 5 + n8 * 6 + n7 * 7 + n6 * 8 + n5 * 9 + n4 * 2 + n3 * 3 + n2 * 4 + n1 * 5

        d1 = 11 - (mod(d1, 11))

        if (d1 >= 10) d1 = 0

        var d2 =
            d1 * 2 + n12 * 3 + n11 * 4 + n10 * 5 + n9 * 6 + n8 * 7 + n7 * 8 + n6 * 9 + n5 * 2 + n4 * 3 + n3 * 4 + n2 * 5 + n1 * 6

        d2 = 11 - (mod(d2, 11))

        if (d2 >= 10) d2 = 0

        var retorno: String? = null

        retorno = if (comPontos) "$n1$n2.$n3$n4$n5.$n6$n7$n8/$n9$n10$n11$n12-$d1$d2"
        else "" + n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + d1 + d2

        return retorno
    }

    fun main() {
        val gerador: GeraCpfCnpj = GeraCpfCnpj()
        val cpf: String = gerador.cpf()
        System.out.printf("CPF: %s, Valido: %s\n", cpf, gerador.isCPF(cpf))

        val cnpj: String = gerador.cnpj()
        System.out.printf("CNPJ: %s, Valido: %s\n", cnpj, gerador.isCNPJ(cnpj))
    }

    private fun pontuacao() {
        comPontos = if (true) true
        else false
    }

    fun isCPF(CPF: String): Boolean {
        var CPF = CPF
        CPF = removeCaracteresEspeciais(CPF)


        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF == "00000000000" || CPF == "11111111111" || CPF == "22222222222" || CPF == "33333333333" || CPF == "44444444444" || CPF == "55555555555" || CPF == "66666666666" || CPF == "77777777777" || CPF == "88888888888" || CPF == "99999999999" || (CPF.length != 11)) return (false)

        val dig10: Char
        val dig11: Char
        var sm: Int
        var i: Int
        var r: Int
        var num: Int
        var peso: Int

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0
            peso = 10
            i = 0
            while (i < 9) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = (CPF[i].code - 48)
                sm = sm + (num * peso)
                peso = peso - 1
                i++
            }

            r = 11 - (sm % 11)
            dig10 = if ((r == 10) || (r == 11)) '0'
            else (r + 48).toChar() // converte no respectivo caractere numerico


            // Calculo do 2o. Digito Verificador
            sm = 0
            peso = 11
            i = 0
            while (i < 10) {
                num = (CPF[i].code - 48)
                sm = sm + (num * peso)
                peso = peso - 1
                i++
            }

            r = 11 - (sm % 11)
            dig11 = if ((r == 10) || (r == 11)) '0'
            else (r + 48).toChar()

            // Verifica se os digitos calculados conferem com os digitos informados.
            return if ((dig10 == CPF[9]) && (dig11 == CPF[10])) true
            else false
        } catch (erro: InputMismatchException) {
            return (false)
        }
    }

    fun isCNPJ(CNPJ: String): Boolean {
        var CNPJ = CNPJ
        CNPJ = removeCaracteresEspeciais(CNPJ)


        // considera-se erro CNPJ's formados por uma sequencia de numeros iguais
        if (CNPJ == "00000000000000" || CNPJ == "11111111111111" || CNPJ == "22222222222222" || CNPJ == "33333333333333" || CNPJ == "44444444444444" || CNPJ == "55555555555555" || CNPJ == "66666666666666" || CNPJ == "77777777777777" || CNPJ == "88888888888888" || CNPJ == "99999999999999" || (CNPJ.length != 14)) return (false)

        val dig13: Char
        val dig14: Char
        var sm: Int
        var i: Int
        var r: Int
        var num: Int
        var peso: Int

        // "try" - protege o código para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0
            peso = 2
            i = 11
            while (i >= 0) {
                // converte o i-ésimo caractere do CNPJ em um número:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posição de '0' na tabela ASCII)
                num = (CNPJ[i].code - 48)
                sm = sm + (num * peso)
                peso = peso + 1
                if (peso == 10) peso = 2
                i--
            }

            r = sm % 11
            dig13 = if ((r == 0) || (r == 1)) '0'
            else ((11 - r) + 48).toChar()

            // Calculo do 2o. Digito Verificador
            sm = 0
            peso = 2
            i = 12
            while (i >= 0) {
                num = (CNPJ[i].code - 48)
                sm = sm + (num * peso)
                peso = peso + 1
                if (peso == 10) peso = 2
                i--
            }

            r = sm % 11
            dig14 = if ((r == 0) || (r == 1)) '0'
            else ((11 - r) + 48).toChar()

            // Verifica se os dígitos calculados conferem com os dígitos informados.
            return if ((dig13 == CNPJ[12]) && (dig14 == CNPJ[13])) true
            else false
        } catch (erro: InputMismatchException) {
            return (false)
        }
    }

    private fun removeCaracteresEspeciais(doc: String): String {
        var doc = doc

        if (doc.contains(".")) {
            doc = doc.replace(".", "")
        }

        if (doc.contains("-")) {
            doc = doc.replace("-", "")
        }

        if (doc.contains("/")) {
            doc = doc.replace("/", "")
        }
        return doc
    }

    fun imprimeCNPJ(CNPJ: String): String {
        // máscara do CNPJ: 99.999.999.9999-99
        return (CNPJ.substring(0, 2) + "." + CNPJ.substring(2, 5) + "." + CNPJ.substring(5, 8) + "." + CNPJ.substring(
            8,
            12
        ) + "-" + CNPJ.substring(12, 14))
    } /*
	var comPontos;

	function randomiza(n) {
	var ranNum = Math.round(Math.random()*n);
	return ranNum;
	}

	function mod(dividendo,divisor) {
	return Math.round(dividendo - (Math.floor(dividendo/divisor)*divisor));
	}

	function cpf() {
	var n = 9;
	var n1 = randomiza(n);
	var n2 = randomiza(n);
	var n3 = randomiza(n);
	var n4 = randomiza(n);
	var n5 = randomiza(n);
	var n6 = randomiza(n);
	var n7 = randomiza(n);
	var n8 = randomiza(n);
	var n9 = randomiza(n);
	var d1 = n9*2+n8*3+n7*4+n6*5+n5*6+n4*7+n3*8+n2*9+n1*10;
	d1 = 11 - ( mod(d1,11) );
	if (d1>=10) d1 = 0;
	var d2 = d1*2+n9*3+n8*4+n7*5+n6*6+n5*7+n4*8+n3*9+n2*10+n1*11;
	d2 = 11 - ( mod(d2,11) );
	if (d2>=10) d2 = 0;
	retorno = '';
	if (comPontos) retorno = ''+n1+n2+n3+'.'+n4+n5+n6+'.'+n7+n8+n9+'-'+d1+d2;
	else retorno = ''+n1+n2+n3+n4+n5+n6+n7+n8+n9+d1+d2;
	return retorno;
	}

	function cnpj() {
	var n = 9;
	var n1 = randomiza(n);
	var n2 = randomiza(n);
	var n3 = randomiza(n);
	var n4 = randomiza(n);
	var n5 = randomiza(n);
	var n6 = randomiza(n);
	var n7 = randomiza(n);
	var n8 = randomiza(n);
	var n9 = 0; //randomiza(n);
	var n10 = 0; //randomiza(n);
	var n11 = 0; //randomiza(n);
	var n12 = 1; //randomiza(n);
	var d1 = n12*2+n11*3+n10*4+n9*5+n8*6+n7*7+n6*8+n5*9+n4*2+n3*3+n2*4+n1*5;
	d1 = 11 - ( mod(d1,11) );
	if (d1>=10) d1 = 0;
	var d2 = d1*2+n12*3+n11*4+n10*5+n9*6+n8*7+n7*8+n6*9+n5*2+n4*3+n3*4+n2*5+n1*6;
	d2 = 11 - ( mod(d2,11) );
	if (d2>=10) d2 = 0;
	retorno = '';
	if (comPontos) retorno = ''+n1+n2+'.'+n3+n4+n5+'.'+n6+n7+n8+'/'+n9+n10+n11+n12+'-'+d1+d2;
	else retorno = ''+n1+n2+n3+n4+n5+n6+n7+n8+n9+n10+n11+n12+d1+d2;
	return retorno;
	}

	function faz() {
	if (document.form1.tipo[0].checked)document.form1.numero.value = cpf();
	else document.form1.numero.value = cnpj();
	}

	function pontuacao() {
	if (document.form1.cbPontos.checked)
	comPontos = true;
	else
	comPontos = false;
	}*/


}