# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.
import sympy as sp

if __name__ == "__main__":
    A1, B1, a1, b1, O1 = sp.symbols('A1, B1, a1, b1, O1')
    A2, B2, a2, b2, O2 = sp.symbols('A2, B2, a2, b2, O2')
    wAa, wAb, wBa, wBb, waA, wbA, waB, wbB, uAa, uAb, uBa, uBb = sp.symbols(\
    'wAa, wAb, wBa, wBb, waA, wbA, waB, wbB, uAa, uAb, uBa, uBb')
    delta = \
    (uAa * wAa * A1 + uAa * waA * a1 - O1) ** 2+(uAb * wAb * A1 + uAb * wbA * b1 - O1) ** 2 + \
    (uBa * wBa * B1 + uBa * waB * a1 - O1) ** 2+(uBb * wBb * B1 + uBb * wbB * b1 - O1) ** 2 + \
    (uAa * wAa * A2 + uAa * waA * a2 - O2) ** 2+(uAb * wAb * A2 + uAb * wbA * b2 - O2) ** 2 + \
    (uBa * wBa * B2 + uBa * waB * a2 - O2) ** 2+(uBb * wBb * B2 + uBb * wbB * b2 - O2) ** 2
    #equ = delta.expand()
    diffs = [delta.diff(u) for u in [wAa, wAb, wBa, wBb, waA, wbA, waB, wbB, uAa, uAb, uBa, uBb]]
#    gb = sp.groebner(diffs, [wAa, wAb, wBa, wBb, waA, wbA, waB, wbB, uAa, uAb, uBa, uBb])
#    for u in diffs : print u.collect([wAa, wAb, wBa, wBb, waA, wbA, waB, wbB, uAa, uAb, uBa, uBb])    
#    for u in diffs : print u
    factors = [uAa, uAb, uBa, uBb, uAa, uAb, uBa, uBb, 1, 1, 1, 1 ]
    print "\n"
    for i in range(12) : print (diffs[i]/(2*factors[i])).expand()